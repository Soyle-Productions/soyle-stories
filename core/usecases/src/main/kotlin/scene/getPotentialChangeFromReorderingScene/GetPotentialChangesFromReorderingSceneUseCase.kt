package com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.CharacterInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.common.AffectedCharacter
import com.soyle.stories.usecase.scene.common.AffectedScene
import com.soyle.stories.usecase.scene.common.PreviousMotivations.Companion.sortedByProjectOrder
import java.util.*
import kotlin.collections.HashMap

class GetPotentialChangesFromReorderingSceneUseCase(
    private val sceneRepository: SceneRepository
) : GetPotentialChangesFromReorderingScene {

    override suspend fun invoke(sceneId: UUID, index: Int, output: GetPotentialChangesFromReorderingScene.OutputPort) {
        val scene = getScene(sceneId)
        val affectedScenes = getScenesAffectedByMovingSceneToIndex(scene, index)
        PotentialChangesFromReorderingScene(affectedScenes)
            .let(output::receivePotentialChangesFromReorderingScene)
    }

    private suspend fun getScene(sceneId: UUID) = (sceneRepository.getSceneById(Scene.Id(sceneId))
        ?: throw SceneDoesNotExist(sceneId))

    private suspend fun getScenesAffectedByMovingSceneToIndex(scene: Scene, index: Int): List<AffectedScene>
    {
        val orderedScenes = sceneRepository.getOrderedScenes(scene.projectId) // A, B, C
        if (index.isSameAsCurrentIndex(orderedScenes, scene)) return listOf() // 2 == 0, 2 == 1
        val reorderedScenes = orderedScenes.moveScene(scene, index) // B, A, C
        return getScenesWithChangedMotivationForCharacters(orderedScenes, reorderedScenes, scene.includedCharacters)
    }

    private suspend fun SceneRepository.getOrderedScenes(projectId: Project.Id): IndexedScenes {
        val scenes = listAllScenesInProject(projectId)
        return IndexedScenes(scenes.sortedByProjectOrder(projectId, this))
    }

    private fun Int.isSameAsCurrentIndex(orderedScenes: IndexedScenes, scene: Scene): Boolean
    {
        val currentIndex = orderedScenes.indexOf(scene.id)
        return this == currentIndex || this == currentIndex + 1
    }


    private fun IndexedScenes.moveScene(scene: Scene, index: Int): IndexedScenes {
        val startIndex = indexOf(scene.id)
        val newList = if (startIndex < index)
            moveForward(index -1, startIndex)
        else moveBackward(index, startIndex)
        return IndexedScenes(newList)
    }

    private fun IndexedScenes.moveBackward(
        index: Int,
        startIndex: Int
    ): List<Scene> {
        return List(size) {
            when {
                it < index -> this[it]
                it == index -> this[startIndex]
                it <= startIndex -> this[it - 1]
                else -> this[it]
            }
        }
    }

    private fun IndexedScenes.moveForward(
        index: Int,
        startIndex: Int
    ): List<Scene> {
        return List(size) {
            when {
                it < startIndex -> this[it]
                it < index -> this[it + 1]
                it == index -> this[startIndex]
                else -> this[it]
            }
        }
    }

    private fun getScenesWithChangedMotivationForCharacters(
        orderedScenes: IndexedScenes,
        reorderedScenes: IndexedScenes,
        characters: Collection<CharacterInScene>
    ): List<AffectedScene> {
        return getAffectedScenesByCharacter(
            orderedScenes,
            reorderedScenes,
            characters
        ).map { (affectedScene, affectedCharacters) ->
            AffectedScene(
                affectedScene.id.uuid,
                affectedScene.name.value,
                affectedCharacters
            )
        }
    }

    private fun getAffectedScenesByCharacter(
        orderedScenes: IndexedScenes,
        reorderedScenes: IndexedScenes,
        characters: Collection<CharacterInScene>
    ) = characters.asSequence()
        .map {
            getAffectedScenesForCharacter(orderedScenes, reorderedScenes, it.characterId).asSequence()
        }
        .flatten()
        .groupBy { it.first }
        .mapValues {
            it.value.map { it.second }
        }

    private fun getAffectedScenesForCharacter(
        orderedScenes: IndexedScenes,
        reorderedScenes: IndexedScenes,
        character: Character.Id
    ) = orderedScenes.mapNotNull {
        val affectOnCharacter = getAffectOnCharacterInScene(orderedScenes, reorderedScenes, character, it)
            ?: return@mapNotNull null
        it to affectOnCharacter
    }

    private fun getAffectOnCharacterInScene(
        orderedScenes: IndexedScenes,
        reorderedScenes: IndexedScenes,
        character: Character.Id,
        scene: Scene
    ): AffectedCharacter? {
        if (!scene.includesCharacter(character)) return null
        val motivation = scene.getMotivationForCharacter(character)!!
        if (!motivation.isInherited()) return null
        val originalInheritedMotive = getInheritedMotiveForCharacterInScene(orderedScenes, character, scene)
        val newInheritedMotive = getInheritedMotiveForCharacterInScene(reorderedScenes, character, scene)
        if (originalInheritedMotive == newInheritedMotive) return null
        return AffectedCharacter(
            character.uuid,
            motivation.characterName,
            originalInheritedMotive ?: "",
            newInheritedMotive ?: ""
        )
    }

    private fun getInheritedMotiveForCharacterInScene(
        scenes: IndexedScenes,
        character: Character.Id,
        scene: Scene
    ): String? {
        val index = scenes.indexOf(scene.id)
        return scenes.getLastSetMotiveForCharacter(character, index)
    }

    private class IndexedScenes(scenes: List<Scene>) : List<Scene> by scenes {

        private val sceneToIndex: Map<Scene.Id, Int> = scenes.withIndex().associate { it.value.id to it.index }

        fun indexOf(scene: Scene.Id): Int = sceneToIndex[scene] ?: -1

        private val lastSetMotiveForCharacterCache = HashMap<Pair<Character.Id, Int>, Int>()

        fun getLastSetMotiveForCharacter(character: Character.Id, from: Int): String? {
            for (i in from downTo 0) {
                val lastSetIndex = lastSetMotiveForCharacterCache[character to i]
                if (lastSetIndex != null) {
                    lastSetMotiveForCharacterCache[character to from] = lastSetIndex
                    return this[lastSetIndex].getMotivationForCharacter(character)!!.motivation
                }
                val scene = this[i]
                if (scene.includesCharacter(character) && ! scene.getMotivationForCharacter(character)!!.isInherited()) {
                    lastSetMotiveForCharacterCache[character to from] = i
                    (i..from).forEach { lastSetMotiveForCharacterCache[character to it] = i }
                    return scene.getMotivationForCharacter(character)!!.motivation
                }
            }
            return null
        }

    }

}