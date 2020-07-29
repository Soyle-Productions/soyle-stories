package com.soyle.stories.scene.usecases

import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.makeCharacter
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
	private val characterName = "${UUID.randomUUID()}"
	private val sceneId = Scene.Id().uuid
	private val projectId = Project.Id()

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

	@Test
	fun `characters previously set motivations`() {
		givenSceneExistsWithStoryEventId()
		givenCharacterExists()
		givenMotivationsForCharacterPreviouslySet()
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
		Scene(Scene.Id(sceneId), projectId, "", StoryEvent.Id(storyEventId), null, listOf()).let {
			sceneRepository.scenes[it.id] = it
			sceneRepository.sceneOrder[projectId] = listOf(it.id)
		}
	}

	private fun givenCharacterExists()
	{
		runBlocking {
			characterRepository.addNewCharacter(makeCharacter(Character.Id(characterId), projectId, characterName))
		}
	}

	private fun givenSceneIncludesCharacter()
	{
		sceneRepository.scenes[Scene.Id(sceneId)]!!.withCharacterIncluded(makeCharacter(Character.Id(characterId), Project.Id(), characterName)).let {
			sceneRepository.scenes[it.id] = it
		}
	}

	private var lastMotiveSource: Scene? = null

	private fun givenMotivationsForCharacterPreviouslySet()
	{
		repeat(5) {
			Scene(projectId, "${UUID.randomUUID()}", StoryEvent.Id())
			  .withCharacterIncluded(characterRepository.characters.values.first())
			  .withMotivationForCharacter(Character.Id(characterId), "${UUID.randomUUID()}")
			  .let {
				  sceneRepository.scenes[it.id] = it
			  }
		}
		val previousIds = sceneRepository.scenes.values.filterNot { it.id.uuid == sceneId }.map { it.id }
		sceneRepository.sceneOrder[projectId] = previousIds + Scene.Id(sceneId)
		lastMotiveSource = sceneRepository.scenes.getValue(previousIds.last())
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
		assertEquals(characterId, actual.characterDetails.characterId)
		assertEquals(characterName, actual.characterDetails.characterName)
		assertNull(actual.characterDetails.motivation)
		assertInheritedMotivation(actual)
	}

	private fun assertInheritedMotivation(actual: IncludeCharacterInScene.ResponseModel)
	{
		val lastMotiveSource = lastMotiveSource ?: return
		assertNotNull(actual.characterDetails.inheritedMotivation)
		assertEquals(lastMotiveSource.id.uuid, actual.characterDetails.inheritedMotivation!!.sceneId)
		assertEquals(lastMotiveSource.name, actual.characterDetails.inheritedMotivation!!.sceneName)
		assertEquals(lastMotiveSource.getMotivationForCharacter(Character.Id(characterId))?.motivation, actual.characterDetails.inheritedMotivation!!.motivation)
	}

}