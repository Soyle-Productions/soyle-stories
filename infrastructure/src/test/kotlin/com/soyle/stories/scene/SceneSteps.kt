package com.soyle.stories.scene

import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import kotlinx.coroutines.runBlocking

object SceneSteps {

	fun getCreatedScenes(double: SoyleStoriesTestDouble): List<Scene>
	{
		val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
		return runBlocking {
			scope.get<SceneRepository>().listAllScenesInProject(Project.Id(scope.projectId))
		}
	}

	fun getNumberOfCreatedScenes(double: SoyleStoriesTestDouble): Int =
	  getCreatedScenes(double).size

}