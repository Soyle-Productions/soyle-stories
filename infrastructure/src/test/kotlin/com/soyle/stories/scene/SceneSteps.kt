package com.soyle.stories.scene

import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*

object SceneSteps {

	fun setNumberOfCreatedScenes(double: SoyleStoriesTestDouble, count: Int) {
		ProjectSteps.givenProjectHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		val createdCount = getNumberOfCreatedScenes(double)
		val controller = scope.get<CreateNewSceneController>()
		repeat(count - createdCount) {
			controller.createNewScene("Unique Scene Name ${UUID.randomUUID()}")
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
		if (getNumberOfCreatedScenes(double) < count)
		{
			setNumberOfCreatedScenes(double, count)
		}
		assertTrue(getNumberOfCreatedScenes(double) >= count)
	}

}