package com.soyle.stories.scene.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.*
import com.soyle.stories.scene.characterMotivations
import com.soyle.stories.scene.doubles.LocaleDouble
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.sceneDoesNotExist
import com.soyle.stories.scene.usecases.common.IncludedCharacterDetails
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetails
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetailsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class GetSceneDetailsUnitTest {

	private val projectId = Project.Id()
	private val sceneId = Scene.Id()
	private val storyEventId = StoryEvent.Id()
	private val linkedLocationId = Location.Id()

	private val includedCharacters = List(5) {
		Scene.CharacterMotivation(
		  Character.Id(),
		  "Character Name: " + UUID.randomUUID().toString(),
		  if (it % 2 == 0) UUID.randomUUID().toString() else null
		)
	}
	private val inheritedMotivations = includedCharacters.map {
		Scene(
		  projectId,
		  "Scene Name: " + UUID.randomUUID().toString(),
		  StoryEvent.Id()
		).withCharacterIncluded(Character(it.characterId, projectId, it.characterName))
		  .withMotivationForCharacter(it.characterId, UUID.randomUUID().toString())
	}

	private var result: Any? = null

	@Test
	fun `scene doesn't exist`() {
		whenSceneDetailsRequested()
		result.shouldBe(sceneDoesNotExist(sceneId.uuid))
	}

	@ParameterizedTest
	@ValueSource(booleans = [true, false])
	fun `linked location is output`(expectLocation: Boolean) {
		givenSceneExists(withLocation = expectLocation)
		whenSceneDetailsRequested()
		result.shouldBe(responseModel(expectLocation = expectLocation))
	}

	@ParameterizedTest
	@ValueSource(booleans = [true, false])
	fun `included characters are output`(includesCharacters: Boolean) {
		givenSceneExists(includesCharacters = includesCharacters)
		whenSceneDetailsRequested()
		result.shouldBe(responseModel(expectCharacters = includesCharacters))
	}

	@Test
	fun `characters previously set motivations`() {
		givenSceneExists(includesCharacters = true, motivationsPreviouslySet = true)
		whenSceneDetailsRequested()
		result.shouldBe(responseModel(expectCharacters = true, expectInheritedMotivations = true))
	}

	private val sceneRepository = SceneRepositoryDouble()

	private fun givenSceneExists(withLocation: Boolean = false, includesCharacters: Boolean = false, motivationsPreviouslySet: Boolean = false)
	{
		val scene = Scene(
		  sceneId, projectId, "", storyEventId, linkedLocationId.takeIf { withLocation },
		  includedCharacters.takeIf { includesCharacters } ?: listOf()
		)
		sceneRepository.scenes[scene.id] = scene
		if (motivationsPreviouslySet) {
			inheritedMotivations.forEach {
				sceneRepository.scenes[it.id] = it
			}
			sceneRepository.sceneOrder[projectId] = inheritedMotivations.map { it.id } + sceneId
		} else {
			sceneRepository.sceneOrder[projectId] = listOf(sceneId)
		}
	}

	private fun whenSceneDetailsRequested()
	{
		val useCase: GetSceneDetails = GetSceneDetailsUseCase(sceneRepository)
		val output = object : GetSceneDetails.OutputPort {
			override fun failedToGetSceneDetails(failure: Exception) {
				result = failure
			}

			override fun sceneDetailsRetrieved(response: GetSceneDetails.ResponseModel) {
				result = response
			}
		}
		runBlocking {
			useCase.invoke(GetSceneDetails.RequestModel(sceneId.uuid, LocaleDouble()), output)
		}
	}

	private fun responseModel(
	  expectLocation: Boolean = false, expectCharacters: Boolean = false, expectInheritedMotivations: Boolean = false
	): (Any?) -> Unit = { actual ->
		actual as GetSceneDetails.ResponseModel
		assertEquals(sceneId.uuid, actual.sceneId)
		assertEquals(storyEventId.uuid, actual.storyEventId)

		if (expectLocation) assertEquals(linkedLocationId.uuid, actual.locationId)
		else assertNull(actual.locationId)

		if (expectCharacters) {
			val expectedCharacters = includedCharacters.associateBy { it.characterId.uuid }
			assertEquals(
			  expectedCharacters.keys,
			  actual.characters.map(IncludedCharacterDetails::characterId).toSet()
			)
			actual.characters.forEach {
				val expectedCharacter = expectedCharacters.getValue(it.characterId)
				assertEquals(expectedCharacter.characterName, it.characterName)
				assertEquals(expectedCharacter.motivation, it.motivation)
			}
			if (expectInheritedMotivations) {
				val motivationSources = inheritedMotivations.associateBy { it.characterMotivations().single().characterId.uuid }
				actual.characters.forEach {
					val motivationSource = motivationSources.getValue(it.characterId)
					assertEquals(motivationSource.id.uuid, it.inheritedMotivation?.sceneId)
					assertEquals(motivationSource.name, it.inheritedMotivation?.sceneName)
					assertEquals(motivationSource.characterMotivations().single().motivation!!, it.inheritedMotivation?.motivation)
				}
			}
		}
		else assertTrue(actual.characters.isEmpty())
	}
}