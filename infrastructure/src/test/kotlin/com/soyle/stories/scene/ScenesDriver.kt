package com.soyle.stories.scene

import com.soyle.stories.DependentProperty
import com.soyle.stories.UATLogger
import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.ScenesDriver.interact
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
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

}