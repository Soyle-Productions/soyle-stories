package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import com.soyle.stories.usecase.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneUseCase
import com.soyle.stories.usecase.storyevent.characterDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SetMotivationForCharacterInSceneUnitTest {

	private val sceneId = Scene.Id()
	private val characterId = Character.Id()
	private val motivationToSet = "I'm the new motivation"

	private var savedScene: Scene? = null
	private var result: Any? = null

	@Test
	fun `happy path`() {
		givenSceneExists(includesCharacter = true)
		givenCharacterExists()
		whenCharacterMotivationIsSet()
		assertMotivationForCharacterUpdatedInScene()
		result.shouldBe(responseModel())
	}

	@Test
	fun `scene doesn't exist`() {
		whenCharacterMotivationIsSet()
		assertNull(savedScene)
		result.shouldBe(sceneDoesNotExist(sceneId.uuid))
	}

	@Test
	fun `character doesn't exist`() {
		givenSceneExists()
		whenCharacterMotivationIsSet()
		assertNull(savedScene)
		result.shouldBe(characterDoesNotExist(characterId.uuid))
	}

	@Test
	fun `character not included in scene`() {
		givenSceneExists()
		givenCharacterExists()
		whenCharacterMotivationIsSet()
		assertNull(savedScene)
		result.shouldBe(characterNotInScene())
	}

	@Test
	fun `motivation already set`() {
		givenSceneExists(includesCharacter = true, hasSameMotivation = true)
		givenCharacterExists()
		whenCharacterMotivationIsSet()
		assertNull(savedScene)
		result.shouldBe(responseModel())
	}

	private val sceneRepository = SceneRepositoryDouble(onUpdateScene = {
		savedScene = it
	})

	private var originalScene: Scene? = null

	private fun givenSceneExists(includesCharacter: Boolean = false, hasSameMotivation: Boolean = false)
	{
		val scene = makeScene(sceneId, Project.Id()).let {
			when {
				includesCharacter && hasSameMotivation -> {
					it.withCharacterIncluded(makeCharacter(characterId, Project.Id(), characterName())).scene
					  .withMotivationForCharacter(characterId, motivationToSet)
				}
				includesCharacter -> it.withCharacterIncluded(makeCharacter(characterId, Project.Id(), characterName())).scene
				else -> it
			}
		}
		originalScene = scene
		sceneRepository.scenes[scene.id] = scene
	}

	private val characterRepository = CharacterRepositoryDouble()

	private fun givenCharacterExists()
	{
		runBlocking {
			characterRepository.addNewCharacter(
				makeCharacter(characterId, Project.Id(), characterName())
			)
		}
	}

	private fun whenCharacterMotivationIsSet() {
		val useCase = SetMotivationForCharacterInSceneUseCase(sceneRepository, characterRepository)
		val output = object : SetMotivationForCharacterInScene.OutputPort {
			override fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
				result = response
			}

			override fun failedToSetMotivationForCharacterInScene(failure: Exception) {
				result = failure
			}
		}
		runBlocking {
			useCase.invoke(
			  SetMotivationForCharacterInScene.RequestModel(sceneId.uuid, characterId.uuid, motivationToSet, SceneLocaleDouble()),
			  output
			)
		}
	}

	private fun assertMotivationForCharacterUpdatedInScene()
	{
		val actual = savedScene as Scene
		assertEquals(originalScene!!.id, actual.id)
		assertEquals(originalScene!!.projectId, actual.projectId)
		assertEquals(originalScene!!.name, actual.name)
		assertEquals(originalScene!!.storyEventId, actual.storyEventId)
		assertEquals(motivationToSet, actual.getMotivationForCharacter(characterId)?.motivation)
	}

	private fun responseModel(): (Any?) -> Unit = { actual ->
		actual as SetMotivationForCharacterInScene.ResponseModel
		assertEquals(sceneId.uuid, actual.sceneId)
		assertEquals(characterId.uuid, actual.characterId)
		assertEquals(motivationToSet, actual.motivation)
	}

	private fun characterNotInScene(): (Any?) -> Unit = { actual ->
		actual as SceneDoesNotIncludeCharacter
		assertEquals(sceneId, actual.sceneId)
		assertEquals(characterId, actual.characterId)
	}

}