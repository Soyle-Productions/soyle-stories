package com.soyle.stories.scene.usecases

import com.soyle.stories.character.doubles.CharacterRepositoryDouble
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.NoSceneExistsWithStoryEventId
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInSceneUseCase
import com.soyle.stories.storyevent.characterDoesNotExist
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class IncludeCharacterInSceneUnitTest {

	private val storyEventId = StoryEvent.Id().uuid
	private val characterId = Character.Id().uuid
	private val sceneId = Scene.Id().uuid

	private var savedScene: Scene? = null
	private var result: Any? = null

	@Test
	fun `scene does not exist`() {
		whenCharacterIncludedInScene()
		assertNull(savedScene)
		result.shouldBe(noSceneExistsWithStoryEventId(storyEventId))
	}

	@Test
	fun `character does not exist`() {
		givenSceneExistsWithStoryEventId()
		whenCharacterIncludedInScene()
		assertNull(savedScene)
		result.shouldBe(characterDoesNotExist(characterId))
	}

	@Test
	fun `scene already has character`() {
		givenSceneExistsWithStoryEventId()
		givenCharacterExists()
		givenSceneIncludesCharacter()
		whenCharacterIncludedInScene()
		assertNull(savedScene)
		result.shouldBe(responseModel())
	}

	@Test
	fun `happy path`() {
		givenSceneExistsWithStoryEventId()
		givenCharacterExists()
		whenCharacterIncludedInScene()
		assertSceneUpdated()
		result.shouldBe(responseModel())
	}

	private val sceneRepository = SceneRepositoryDouble(onUpdateScene = {
		savedScene = it
	})
	private val characterRepository = CharacterRepositoryDouble()

	private fun givenSceneExistsWithStoryEventId()
	{
		Scene(Scene.Id(sceneId), Project.Id(), "", StoryEvent.Id(storyEventId), mapOf()).let {
			sceneRepository.scenes[it.id] = it
		}
	}

	private fun givenCharacterExists()
	{
		runBlocking {
			characterRepository.addNewCharacter(Character(Character.Id(characterId), Project.Id().uuid, "Bob"))
		}
	}

	private fun givenSceneIncludesCharacter()
	{
		runBlocking {
			sceneRepository.getSceneForStoryEvent(StoryEvent.Id(storyEventId))!!.withCharacterIncluded(Character.Id(characterId)).let {
				sceneRepository.scenes[it.id] = it
			}
		}
	}

	private fun whenCharacterIncludedInScene() {
		val useCase = IncludeCharacterInSceneUseCase(sceneRepository, characterRepository)
		val output = object : IncludeCharacterInScene.OutputPort {
			override fun failedToIncludeCharacterInScene(failure: Exception) {
				result = failure
			}

			override fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
				result = response
			}
		}
		runBlocking {
			useCase.invoke(
			  AddCharacterToStoryEvent.ResponseModel(storyEventId, characterId),
			  output
			)
		}
	}

	private fun Any?.shouldBe(assertion: (Any?) -> Unit) = assertion(this)

	private fun noSceneExistsWithStoryEventId(storyEventId: UUID): (Any?) -> Unit = { actual ->
		actual as NoSceneExistsWithStoryEventId
		assertEquals(storyEventId, actual.storyEventId)

	}

	private fun assertSceneUpdated()
	{
		val actual = savedScene as Scene
		assertEquals(sceneId, actual.id.uuid)
		assertTrue(actual.includesCharacter(Character.Id(characterId)))
	}

	private fun responseModel(): (Any?) -> Unit = { actual ->

		actual as IncludeCharacterInScene.ResponseModel
		assertEquals(sceneId, actual.sceneId)
		assertEquals(characterId, actual.characterId)

	}

}