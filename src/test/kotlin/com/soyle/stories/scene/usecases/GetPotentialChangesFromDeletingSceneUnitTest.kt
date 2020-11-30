package com.soyle.stories.scene.usecases

import com.soyle.stories.character.characterName
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.charactersInScene
import com.soyle.stories.scene.doubles.LocaleDouble
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.sceneDoesNotExist
import com.soyle.stories.scene.usecases.common.AffectedScene
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*

class GetPotentialChangesFromDeletingSceneUnitTest {

	private val sceneId = Scene.Id()

	private var result: Any? = null

	@BeforeEach
	fun `clean up`() {
		result = null
		sceneRepository.scenes.clear()
		sceneRepository.sceneOrder.clear()
	}

	@AfterEach
	fun `ensure valid output`() {
		val response = result as? GetPotentialChangesFromDeletingScene.ResponseModel ?: return
		response.affectedScenes.forEach {
			assertFalse(it.characters.isEmpty()) { "Scenes without affected characters should not be in output" }
			it.characters.forEach {
				assertEquals(motivationForCharacterSetInScene(it.characterId), it.currentMotivation)
			}
		}
	}

	@Test
	fun `scene does not exist`() {
		potentialChangesFromDeletingScene()
		result.shouldBe(sceneDoesNotExist(sceneId.uuid))
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	inner class `No Affected Scenes` {

		@AfterEach
		fun `output is empty`() {
			result.shouldBe(empty())
		}

		@Test
		fun `no characters in scene`()
		{
			givenScenes(
			  listOf("scene")
			)
			givenDeletedSceneIs { first() }
			potentialChangesFromDeletingScene()
		}

		@Test
		fun `all characters in deleting scene inherit motivation`()
		{
			givenScenes(
			  listOf("scene"),
			  listOf("inherit"),
			  listOf("inherit"),
			  listOf("inherit")
			)
			givenDeletedSceneIs { first() }
			potentialChangesFromDeletingScene()
		}

		@Test
		fun `no other scenes`()
		{
			givenScenes(
			  listOf("scene"),
			  listOf("value 1"),
			  listOf("value 2"),
			  listOf("value 3")
			)
			givenDeletedSceneIs { first() }
			potentialChangesFromDeletingScene()
		}


		@Test
		fun `no scenes following deleting scene`()
		{
			givenScenes(
			  listOf("scene 1", "scene"),
			  listOf("value 1", "value 1"),
			  listOf("value 1", "value 2"),
			  listOf("value 1", "value 3")
			)
			givenDeletedSceneIs { component2() }
			potentialChangesFromDeletingScene()
		}

		@Test
		fun `no characters in scenes following deleting scene`()
		{
			givenScenes(
			  listOf("scene",  "scene 2"),
			  listOf("value 1", "-"),
			  listOf("value 2", "-"),
			  listOf("value 3", "-")
			)
			givenDeletedSceneIs { first() }
			potentialChangesFromDeletingScene()
		}

		@Test
		fun `no shared characters in scenes following deleting scene`()
		{
			givenScenes(
			  listOf("scene",  "scene 2"),
			  listOf("value 1", "-"),
			  listOf("-",       "value 2"),
			  listOf("-",       "value 3")
			)
			givenDeletedSceneIs { first() }
			potentialChangesFromDeletingScene()
		}

		@Test
		fun `all characters in following scenes have set motivations`()
		{
			givenScenes(
			  listOf("scene",  "scene 2"),
			  listOf("value 1", "value 4"),
			  listOf("value 2", "value 5"),
			  listOf("value 3", "value 6")
			)
			givenDeletedSceneIs { first() }
			potentialChangesFromDeletingScene()
		}
	}

	@Nested
	inner class `One Character` {

		@AfterEach
		fun `contains one character`() {
			result.shouldBe(responseModel(sceneCount = 1, characterCountPer = 1))
		}

		@Test
		fun `one character`() {
			givenScenes(
			  listOf("scene 1", "scene 2"),
			  listOf("value 1", "inherit")
			)
			givenDeletedSceneIs { first() }
			potentialChangesFromDeletingScene()
		}

		@Test
		fun `characters not in affected scenes are unaffected`() {
			givenScenes(
			  listOf("scene 1", "scene 2"),
			  listOf("value 1", "inherit"),
			  listOf("value 1", "-")
			)
			givenDeletedSceneIs { first() }
			potentialChangesFromDeletingScene()
		}

		@Test
		fun `scenes without affected character are not in output`() {
			givenScenes(
			  listOf("scene 1", "scene 2", "scene 3"),
			  listOf("value 1", "-",       "inherit")
			)
			givenDeletedSceneIs { first() }
			potentialChangesFromDeletingScene()
		}
	}

	@Test
	fun `scenes inheriting from other scenes are not in output`() {
		givenScenes(
		  listOf("scene 1", "scene 2", "scene 3"),
		  listOf("value 1", "value 2", "inherit")
		)
		givenDeletedSceneIs { first() }
		potentialChangesFromDeletingScene()
		result.shouldBe(empty())
	}

	@Test
	fun `all scenes inheriting from scene are in output`() {
		givenScenes(
		  listOf("scene 1", "scene 2", "scene 3"),
		  listOf("value 1", "inherit", "inherit")
		)
		givenDeletedSceneIs { first() }
		potentialChangesFromDeletingScene()
		result.shouldBe(responseModel(sceneCount = 2, characterCountPer = 1))
	}

	@Test
	fun `no scenes before deleted scene`() {
		givenScenes(
		  listOf("scene 1", "scene 2"),
		  listOf("value 1", "inherit")
		)
		givenDeletedSceneIs { first() }
		potentialChangesFromDeletingScene()
		result.shouldBe {
			it as GetPotentialChangesFromDeletingScene.ResponseModel
			assertEquals("", it.affectedScenes.single().characters.single().potentialMotivation) { "Changed motivation should be empty" }
		}
	}

	@Test
	fun `scene before with character`() {
		givenScenes(
		  listOf("scene 1", "scene 2", "scene 3"),
		  listOf("value 1", "value 2", "inherit")
		)
		givenDeletedSceneIs { component2() }
		potentialChangesFromDeletingScene()
		result.shouldBe {
			it as GetPotentialChangesFromDeletingScene.ResponseModel
			assertEquals("value 1", it.affectedScenes.single().characters.single().potentialMotivation) { "Changed motivation should inherit from previous scene" }
		}
	}

	@Nested
	inner class `Common Scenarios` {

		@BeforeEach
		fun setup() {
			givenScenes(
			  listOf("scene a", "scene b", "scene c", "scene d", "scene e"),
			  listOf("value 1",       "-", "inherit",       "-", "inherit"),
			  listOf(      "-", "inherit",       "-", "value 4", "inherit"),
			  listOf(      "-", "value 2", "value 3",       "-", "inherit"),
			  listOf("inherit",       "-",       "-", "inherit", "inherit")
			)
		}

		@Test
		fun `scene a`() {
			givenDeletedSceneIs { component1() }
			potentialChangesFromDeletingScene()
			result.shouldBe(responseModel(sceneCount = 2, characterCountPer = 1))
		}

		@Test
		fun `scene b`() {
			givenDeletedSceneIs { component2() }
			potentialChangesFromDeletingScene()
			result.shouldBe(empty())
		}

		@Test
		fun `scene c`() {
			givenDeletedSceneIs { component3() }
			potentialChangesFromDeletingScene()
			result.shouldBe(responseModel(sceneCount = 1, characterCountPer = 1))
			result.shouldBe {
				it as GetPotentialChangesFromDeletingScene.ResponseModel
				assertEquals("value 2", it.affectedScenes.single().characters.single().potentialMotivation) { "Changed motivation should inherit from previous scene" }
			}
		}

		@Test
		fun `scene d`() {
			givenDeletedSceneIs { component4() }
			potentialChangesFromDeletingScene()
			result.shouldBe(responseModel(sceneCount = 1, characterCountPer = 1))
		}

		@Test
		fun `scene e`() {
			givenDeletedSceneIs { component5() }
			potentialChangesFromDeletingScene()
			result.shouldBe(empty())
		}
	}

	private val projectId = Project.Id()
	private val sceneRepository = SceneRepositoryDouble()

	private fun givenScenes(sceneNames: List<String>, vararg characterMotives: List<String>)
	{
		val characters = characterMotives.map {
			makeCharacter(Character.Id(), projectId, characterName())
		}
		val scenes = sceneNames.map {
			Scene(projectId, NonBlankString.create(it)!!, StoryEvent.Id())
		}.mapIndexed { col, it ->
			characters.withIndex().fold(it) { scene, (row, character) ->
				val motive = characterMotives[row][col]
				val motiveInScene = when (motive) {
					"-" -> return@fold scene
					"inherit" -> null
					else -> motive
				}
				scene.withCharacterIncluded(character).withMotivationForCharacter(character.id, motiveInScene)
			}
		}
		sceneRepository.sceneOrder[projectId] = scenes.map(Scene::id)
		sceneRepository.scenes.putAll(scenes.associateBy(Scene::id))
	}

	private var targetScene: Scene? = null

	private fun givenDeletedSceneIs(selector: List<Scene>.() -> Scene)
	{
		val orderOf = sceneRepository.sceneOrder.getValue(projectId).withIndex().associate { it.value to it.index }
		val orderedScenes = sceneRepository.scenes.values.filter { it.projectId == projectId }.sortedBy {
			orderOf.getValue(it.id)
		}
		val targetScene = orderedScenes.selector()
		sceneRepository.scenes.remove(targetScene.id)
		sceneRepository.scenes[sceneId] = Scene(sceneId, targetScene.projectId, targetScene.name, targetScene.storyEventId, null, targetScene.charactersInScene())
		sceneRepository.sceneOrder[projectId] = sceneRepository.sceneOrder.getValue(projectId).map {
			if (it == targetScene.id) sceneId
			else it
		}
		this.targetScene = sceneRepository.scenes[sceneId]
	}

	private fun potentialChangesFromDeletingScene()
	{
		val useCase = GetPotentialChangesFromDeletingSceneUseCase(sceneRepository)
		val output = object : GetPotentialChangesFromDeletingScene.OutputPort {
			override fun receivePotentialChangesFromDeletingScene(response: GetPotentialChangesFromDeletingScene.ResponseModel) {
				result = response
			}

			override fun failedToGetPotentialChangesFromDeletingScene(failure: Exception) {
				result = failure
			}
		}
		runBlocking {
			useCase.invoke(GetPotentialChangesFromDeletingScene.RequestModel(sceneId.uuid, LocaleDouble()), output)
		}
	}

	private fun motivationForCharacterSetInScene(characterId: UUID): String?
	{
		return targetScene?.getMotivationForCharacter(Character.Id(characterId))?.motivation
	}

	private fun empty(): (Any?) -> Unit = { actual ->
		actual as GetPotentialChangesFromDeletingScene.ResponseModel
		assertTrue(actual.affectedScenes.isEmpty()) { "Affected Scenes should be empty" }
	}

	private fun responseModel(
	  sceneCount: Int,
	  characterCountPer: Int? = null,
	  characterCount: ((AffectedScene) -> Int)? = null
	): (Any?) -> Unit = {
		actual ->

		actual as GetPotentialChangesFromDeletingScene.ResponseModel
		assertEquals(sceneCount, actual.affectedScenes.size)
		if (characterCountPer != null) {
			actual.affectedScenes.forEach {
				assertEquals(characterCountPer, it.characters.size)
			}
		}
		if (characterCount != null) {
			actual.affectedScenes.forEach {
				assertEquals(characterCount(it), it.characters.size)
			}
		}
	}
}