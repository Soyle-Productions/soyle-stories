package com.soyle.stories.scene.usecases

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.characterNotInScene
import com.soyle.stories.scene.doubles.LocaleDouble
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.sceneDoesNotExist
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RemoveCharacterFromSceneUnitTest {

	private val sceneId = Scene.Id()
	private val storyEventId = StoryEvent.Id()
	private val characterId = Character.Id()

	private var updatedScene: Scene? = null
	private var result: Any? = null

	@Test
	fun `scene does not exist`() {
		whenCharacterIsRemovedFromScene()
		assertNull(updatedScene)
		sceneDoesNotExist(sceneId.uuid).invoke(result)
	}

	@Test
	fun `no scene exists with story event id`() {
		whenCharacterIsRemovedFromSceneWithStoryEventId()
		assertNull(updatedScene)
		assertNull(result)
	}

	@Test
	fun `scene does not have character`() {
		givenSceneExists()
		whenCharacterIsRemovedFromScene()
		assertNull(updatedScene)
		characterNotInScene(sceneId.uuid, characterId.uuid).invoke(result)
	}

	@Test
	fun `scene with story event id does not have character`() {
		givenSceneExists()
		whenCharacterIsRemovedFromSceneWithStoryEventId()
		assertNull(updatedScene)
		assertNull(result)
	}

	@ParameterizedTest
	@ValueSource(booleans = [true, false])
	fun `scene has character`(useStoryEvent: Boolean) {
		givenSceneExists(includesCharacter = true)
		if (useStoryEvent) whenCharacterIsRemovedFromSceneWithStoryEventId()
		else whenCharacterIsRemovedFromScene()
		updatedScene().invoke(updatedScene)
		responseModel().invoke(result)
	}

	private val sceneRepository = SceneRepositoryDouble(onUpdateScene = { updatedScene = it })

	private fun givenSceneExists(includesCharacter: Boolean = false)
	{
		sceneRepository.scenes[sceneId] = Scene(sceneId, Project.Id(), "", storyEventId, null, listOfNotNull(
		  characterId.takeIf { includesCharacter }?.let { Scene.CharacterMotivation(it, "", null) }
		))
	}

	private fun whenCharacterIsRemovedFromScene()
	{
		whenUseCaseIsExecuted {
			removeCharacterFromScene(sceneId.uuid, LocaleDouble(), characterId.uuid, it)
		}
	}
	private fun whenCharacterIsRemovedFromSceneWithStoryEventId()
	{
		whenUseCaseIsExecuted {
			removeCharacterFromSceneWithStoryEventId(storyEventId.uuid, LocaleDouble(), characterId.uuid, it)
		}
	}

	private fun whenUseCaseIsExecuted(execute: suspend RemoveCharacterFromScene.(RemoveCharacterFromScene.OutputPort) -> Unit)
	{
		val useCase: RemoveCharacterFromScene = RemoveCharacterFromSceneUseCase(sceneRepository)
		val output = object : RemoveCharacterFromScene.OutputPort {
			override fun failedToRemoveCharacterFromScene(failure: Exception) {
				result = failure
			}

			override fun characterRemovedFromScene(response: RemoveCharacterFromScene.ResponseModel) {
				result = response
			}
		}
		runBlocking {
			useCase.execute(output)
		}
	}

	private fun updatedScene(): (Any?) -> Unit = { actual ->
		actual as Scene
		assertEquals(sceneId, actual.id)
		assertFalse(actual.includesCharacter(characterId))
	}

	private fun responseModel(): (Any?) -> Unit = { actual ->
		actual as RemoveCharacterFromScene.ResponseModel
		assertEquals(sceneId.uuid, actual.sceneId)
		assertEquals(characterId.uuid, actual.characterId)
	}

}