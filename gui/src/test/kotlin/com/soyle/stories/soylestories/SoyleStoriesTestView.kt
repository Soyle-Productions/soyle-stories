package com.soyle.stories.soylestories

import com.soyle.stories.project.projectList.ProjectListViewListener
import kotlinx.coroutines.runBlocking

class SoyleStoriesTestView(
	private val projectListViewListener: ProjectListViewListener
) {

	fun start(parameters: List<String> = emptyList()) {
		runBlocking {
			projectListViewListener.startApplicationWithParameters(parameters)
		}
	}
}