package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.exceptions.scene.assertThrowsSceneDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.character.listIncluded.ListCharactersInScene
import com.soyle.stories.usecase.scene.character.listIncluded.ListCharactersInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `List Characters in Scene Unit Test` {

    private val projectId = Project.Id()
    private val includedCharacters = List(5) { makeCharacter(projectId = projectId) }
    private val scene = includedCharacters.fold(makeScene(projectId = projectId)) { newScene, character ->
        newScene.withCharacterIncluded(character).scene
    }

    private val sceneRepository = SceneRepositoryDouble()

    @Test
    fun `scene does not exist`() {
        assertThrowsSceneDoesNotExist(scene.id) {
            listCharactersInScene()
        }
    }

    @Nested
    inner class `Given Scene Exists` {

        private val includedCharactersById = includedCharacters.associateBy { it.id }

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `should output included characters`() {
            val result = listCharactersInScene()
            result.charactersInScene.forEach { it.sceneId.mustEqual(scene.id) }
            result.charactersInScene.map { it.characterId }.toSet().mustEqual(includedCharacters.map { it.id }.toSet())
            result.charactersInScene.forEach { assertNull(it.roleInScene) }
        }

        @Test
        fun `should output character names`() {
            val result = listCharactersInScene()
            result.charactersInScene.forEach {
                val backingCharacter = includedCharactersById.getValue(it.characterId)
                it.characterName.mustEqual(backingCharacter.name.value)
            }
        }

        @Test
        fun `should output character desire`() {
            val firstCharacter = scene.includedCharacters.getOrError(includedCharacters.first().id)
            sceneRepository.givenScene(scene.withDesireForCharacter(firstCharacter.id, "Desire 1482").scene)
            val result = listCharactersInScene()
            result.charactersInScene.forEach {
                if (it.characterId == firstCharacter.id) it.desire.mustEqual("Desire 1482")
                else it.desire.mustEqual("")
            }
        }

        @Test
        fun `should output character motivation`() {
            val firstCharacter = scene.includedCharacters.getOrError(includedCharacters.first().id)
            sceneRepository.givenScene(scene.withMotivationForCharacter(firstCharacter.id, "Motivation 832"))
            val result = listCharactersInScene()
            result.charactersInScene.forEach {
                if (it.characterId == firstCharacter.id) it.motivation.mustEqual("Motivation 832")
                else assertNull(it.motivation)
            }
        }

        @Test
        fun `should output previous motivations`() {
            val inheritedScene = makeScene(projectId = projectId)
                .withCharacterIncluded(includedCharacters.first()).scene
                .withMotivationForCharacter(includedCharacters.first().id, "Previous Motive")
            sceneRepository.givenScene(inheritedScene)
            sceneRepository.sceneOrder[scene.projectId] = listOf(inheritedScene.id, scene.id)
            val result = listCharactersInScene()
            result.charactersInScene.find { it.characterId == includedCharacters.first().id }!!
                .inheritedMotivation!!.run {
                    motivation.mustEqual("Previous Motive")
                    sceneId.mustEqual(inheritedScene.id.uuid)
                    sceneName.mustEqual(inheritedScene.name.value)
                }
        }

        @Nested
        inner class `Given Characters have roles`
        {

            private val incitingCharacterId = includedCharacters[3].id
            private val opponentCharacterIds = listOf(includedCharacters[1].id, includedCharacters[4].id)

            init {
                sceneRepository.givenScene(
                    scene.withRoleForCharacter(incitingCharacterId, RoleInScene.IncitingCharacter).scene
                        .withRoleForCharacter(opponentCharacterIds[0], RoleInScene.OpponentCharacter).scene
                        .withRoleForCharacter(opponentCharacterIds[1], RoleInScene.OpponentCharacter).scene
                )
            }

            @Test
            fun `should output character roles`() {
                val result = listCharactersInScene()
                result.charactersInScene.forEach {
                    val backingCharacter = includedCharactersById.getValue(it.characterId)
                    if (backingCharacter.id == incitingCharacterId) it.roleInScene.mustEqual(RoleInScene.IncitingCharacter)
                    else if (backingCharacter.id in opponentCharacterIds) it.roleInScene.mustEqual(RoleInScene.OpponentCharacter)
                    else assertNull(it.roleInScene)
                }
            }

        }

    }

    private fun listCharactersInScene(): ListCharactersInScene.ResponseModel = runBlocking {
        val useCase: ListCharactersInScene = ListCharactersInSceneUseCase(sceneRepository)
        val output = object : ListCharactersInScene.OutputPort {
            lateinit var result: ListCharactersInScene.ResponseModel
            override suspend fun receiveCharactersInScene(response: ListCharactersInScene.ResponseModel) {
                result = response
            }
        }
        useCase.invoke(scene.id, output)
        output.result
    }

}