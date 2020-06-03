package com.soyle.stories.scene.usecases

import com.soyle.stories.character.doubles.CharacterRepositoryDouble
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.CharacterNotInScene
import com.soyle.stories.scene.doubles.LocaleDouble
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.sceneDoesNotExist
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneUseCase
import com.soyle.stories.storyevent.characterDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class SetMotivationForCharacterInSceneUnitTest {

	private val sceneId = Scene.Id().uuid
	private val characterId = Character.Id().uuid
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
		result.shouldBe(sceneDoesNotExist(sceneId))
	}

	@Test
	fun `character doesn't exist`() {
		givenSceneExists()
		whenCharacterMotivationIsSet()
		assertNull(savedScene)
		result.shouldBe(characterDoesNotExist(characterId))
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
		val scene = Scene(Scene.Id(sceneId), Project.Id(), "Scene Name 42", StoryEvent.Id(), mapOf()).let {
			when {
				includesCharacter && hasSameMotivation -> {
					it.withCharacterIncluded(Character.Id(characterId))
					  .withMotivationForCharacter(Character.Id(characterId), motivationToSet)
				}
				includesCharacter -> it.withCharacterIncluded(Character.Id(characterId))
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
			  Character(Character.Id(characterId), UUID.randomUUID(), "Bob")
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
			  SetMotivationForCharacterInScene.RequestModel(sceneId, characterId, motivationToSet, LocaleDouble()),
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
		assertEquals(motivationToSet, actual.getMotivationForCharacter(Character.Id(characterId)))
	}

	private fun responseModel(): (Any?) -> Unit = { actual ->
		actual as SetMotivationForCharacterInScene.ResponseModel
		assertEquals(sceneId, actual.sceneId)
		assertEquals(characterId, actual.characterId)
		assertEquals(motivationToSet, actual.motivation)
	}

	private fun characterNotInScene(): (Any?) -> Unit = { actual ->
		actual as CharacterNotInScene
		assertEquals(sceneId, actual.sceneId)
		assertEquals(characterId, actual.characterId)
	}

}