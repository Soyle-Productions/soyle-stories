package com.soyle.stories.scene

import com.soyle.stories.DependentProperty
import com.soyle.stories.UATLogger
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.ScenesDriver.interact
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

object ScenesDriver : ApplicationTest() {

	fun setNumberOfCreatedScenes(double: SoyleStoriesTestDouble, count: Int) {
		ProjectSteps.givenProjectHasBeenOpened(double)
		val createdCount = getNumberOfCreatedScenes(double)
		repeat(count - createdCount) {
			whenSceneIsCreated(double)
		}
	}

	fun getCreatedScenes(double: SoyleStoriesTestDouble): List<Scene>
	{
		val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
		return runBlocking {
			scope.get<SceneRepository>().listAllScenesInProject(Project.Id(scope.projectId))
		}
	}

	fun getNumberOfCreatedScenes(double: SoyleStoriesTestDouble): Int =
	  getCreatedScenes(double).size

	fun whenSceneIsCreated(double: SoyleStoriesTestDouble)
	{
		val scope = ProjectSteps.getProjectScope(double)!!
		val controller = scope.get<CreateNewSceneController>()
		controller.createNewScene("Unique Scene Name ${UUID.randomUUID()}")
	}

	fun givenNumberOfCreatedScenesIsAtLeast(double: SoyleStoriesTestDouble, count: Int)
	{
		UATLogger.log("Given Number of Created Scenes is At Least $count")
		if (getNumberOfCreatedScenes(double) < count)
		{
			setNumberOfCreatedScenes(double, count)
		}
		assertTrue(getNumberOfCreatedScenes(double) >= count)
	}

	fun createdSceneBefore(sceneId: Scene.Id) = object : DependentProperty<Scene> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  { double: SoyleStoriesTestDouble -> givenNumberOfCreatedScenesIsAtLeast(double, 1) } as (SoyleStoriesTestDouble) -> Unit
		)

		override fun get(double: SoyleStoriesTestDouble): Scene? {
			return null
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			val controller = scope.get<CreateNewSceneController>()
			interact {
				controller.createNewSceneBefore("Unique Scene Name ${UUID.randomUUID()}", sceneId.uuid.toString())
			}
		}
	}

	fun createdSceneAfter(sceneId: Scene.Id) = object : DependentProperty<Scene> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  { double: SoyleStoriesTestDouble -> givenNumberOfCreatedScenesIsAtLeast(double, 1) } as (SoyleStoriesTestDouble) -> Unit
		)

		override fun get(double: SoyleStoriesTestDouble): Scene? {
			return null
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			val controller = scope.get<CreateNewSceneController>()
			interact {
				controller.createNewSceneAfter("Unique Scene Name ${UUID.randomUUID()}", sceneId.uuid.toString())
			}
		}
	}

	fun whenSceneIsDeleted(double: SoyleStoriesTestDouble)
	{
		val scope = ProjectSteps.getProjectScope(double)!!
		val controller = scope.get<DeleteSceneController>()
		val repository = scope.get<SceneRepository>()
		val sceneId = runBlocking {
			repository.listAllScenesInProject(Project.Id(scope.projectId)).first().id
		}
		controller.deleteScene(sceneId.uuid.toString())
	}

	fun characterIncludedIn(characterId: Character.Id, sceneId: Scene.Id) = object : DependentProperty<Nothing> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps::givenProjectHasBeenOpened
		)

		override fun get(double: SoyleStoriesTestDouble): Nothing? = null

		override fun check(double: SoyleStoriesTestDouble): Boolean {
			val scope = ProjectSteps.getProjectScope(double) ?: return false
			val scene = runBlocking {
				scope.get<SceneRepository>().getSceneById(sceneId)
			} ?: return false
			val storyEvent = runBlocking {
				scope.get<StoryEventRepository>().getStoryEventById(scene.storyEventId)
			} ?: return false
			return storyEvent.includedCharacterIds.contains(characterId)
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			val scene = runBlocking {
				scope.get<SceneRepository>().getSceneById(sceneId)
			}!!
			val storyEvent = runBlocking {
				scope.get<StoryEventRepository>().getStoryEventById(scene.storyEventId)
			}!!
			interact {
				scope.get<AddCharacterToStoryEventController>().addCharacterToStoryEvent(storyEvent.id.uuid.toString(), characterId.uuid.toString())
			}
		}
	}

}