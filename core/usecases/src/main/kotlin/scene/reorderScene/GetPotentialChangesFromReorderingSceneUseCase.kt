package com.soyle.stories.usecase.scene.reorderScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.character.CharacterInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.scene.order.SceneOrderService
import com.soyle.stories.domain.scene.order.UnSuccessfulSceneOrderUpdate
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import com.soyle.stories.usecase.scene.character.list.CharactersInScene
import com.soyle.stories.usecase.scene.character.list.PreviousMotivations
import com.soyle.stories.usecase.scene.common.AffectedCharacter
import com.soyle.stories.usecase.scene.common.AffectedScene
import com.soyle.stories.usecase.scene.character.list.PreviousMotivations.Companion.sortedByProjectOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.coroutineContext

class GetPotentialChangesFromReorderingSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val characters: CharacterRepository
) : GetPotentialChangesFromReorderingScene {

    override suspend fun invoke(
        sceneId: Scene.Id,
        index: Int,
        output: GetPotentialChangesFromReorderingScene.OutputPort
    ): Throwable? {
        val scene = sceneRepository.getSceneById(sceneId)
            ?: return SceneDoesNotExist(sceneId.uuid)

        val sceneOrder = sceneRepository.getSceneIdsInOrder(scene.projectId)!!

        val orderUpdate = sceneOrder.withScene(scene.id)!!.movedTo(index)

        if (orderUpdate is UnSuccessfulSceneOrderUpdate) {
            return orderUpdate.reason
        }

        val (currentOrder, newOrder) = coroutineScope {
            val allScenes = async { sceneRepository.listAllScenesInProject(scene.projectId) }
            Pair(
                OrderedScenes(sceneOrder, allScenes, this),
                OrderedScenes(orderUpdate.sceneOrder, allScenes, this)
            )
        }

        output.receivePotentialChangesFromReorderingScene(
            PotentialChangesFromReorderingScene(
                getScenesWithChangedMotivationForCharacters(currentOrder, newOrder, scene.includedCharacters)
            )
        )

        return null
    }

    private suspend fun getScenesWithChangedMotivationForCharacters(
        orderedScenes: OrderedScenes,
        reorderedScenes: OrderedScenes,
        characters: Collection<CharacterInScene>
    ): List<AffectedScene> {
        return getAffectedScenesByCharacter(
            orderedScenes,
            reorderedScenes,
            characters
        ).map { (affectedScene, affectedCharacters) ->
            AffectedScene(
                affectedScene.id,
                affectedScene.name.value,
                affectedCharacters
            )
        }
    }

    private suspend fun getAffectedScenesByCharacter(
        orderedScenes: OrderedScenes,
        reorderedScenes: OrderedScenes,
        charactersInScene: Collection<CharacterInScene>
    ) = charactersInScene.asFlow()
        .mapNotNull { characters.getCharacterById(it.id) }
        .flatMapConcat{ getAffectedScenesForCharacter(orderedScenes, reorderedScenes, it).asFlow() }
        .toList()
        .groupBy { it.first }
        .mapValues { it.value.map { it.second } }

    private suspend fun getAffectedScenesForCharacter(
        orderedScenes: OrderedScenes,
        reorderedScenes: OrderedScenes,
        character: Character
    ) = orderedScenes().mapNotNull {
        val affectOnCharacter = getAffectOnCharacterInScene(orderedScenes, reorderedScenes, character, it)
            ?: return@mapNotNull null
        it to affectOnCharacter
    }

    private suspend fun getAffectOnCharacterInScene(
        orderedScenes: OrderedScenes,
        reorderedScenes: OrderedScenes,
        character: Character,
        scene: Scene
    ): AffectedCharacter? {
        if (!scene.includesCharacter(character.id)) return null
        val motivation = scene.getMotivationForCharacter(character.id)!!
        if (!motivation.isInherited()) return null
        val originalInheritedMotive = orderedScenes.getLastSetMotivation(character.id, scene.id)
        val newInheritedMotive = reorderedScenes.getLastSetMotivation(character.id, scene.id)
        if (originalInheritedMotive == newInheritedMotive) return null
        return AffectedCharacter(
            character.id.uuid,
            character.displayName.value,
            originalInheritedMotive ?: "",
            newInheritedMotive ?: ""
        )
    }

    private class OrderedScenes(
        private val sceneOrder: SceneOrder,
        private val allScenes: Deferred<List<Scene>>,
        scope: CoroutineScope
    ) {

        private val sceneIndices by lazy { sceneOrder.order.withIndex().associate { it.value to it.index } }

        private val sortedScenes = scope.async {
            allScenes.await().sortedBy { sceneIndices.getValue(it.id) }
        }

        suspend operator fun invoke() = sortedScenes.await()

        private val characterMotivationCache = mutableMapOf<Character.Id, MutableMap<Int, String>>()
        private fun cacheAtPosition(characterId: Character.Id, position: Int, motive: String) {
            characterMotivationCache.getOrPut(characterId, ::mutableMapOf)[position] = motive
        }

        suspend fun getLastSetMotivation(characterId: Character.Id, from: Scene.Id): String? {
            return getLastSetMotivation(characterId, sceneIndices.getValue(from))
        }
        private suspend fun getLastSetMotivation(characterId: Character.Id, from: Int): String? {
            if (from < 0) return null
            val cached = characterMotivationCache.getOrPut(characterId, ::mutableMapOf)[from]
            if (cached != null) return cached
            val scene = sortedScenes.await()[from]
            val characterInScene = scene.includedCharacters[characterId]
            val setMotivation = characterInScene?.motivation
            if (setMotivation != null) return setMotivation.also { cacheAtPosition(characterId, from, it) }
            return getLastSetMotivation(characterId, from - 1)?.also { cacheAtPosition(characterId, from, it) }
        }

    }

}