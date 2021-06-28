package com.soyle.stories.project.projectList.presenters

import com.soyle.stories.project.*
import com.soyle.stories.project.startNewProject.ProjectStartedReceiver
import com.soyle.stories.usecase.project.startNewProject.StartNewProject

internal class StartNewLocalProjectPresenter(
) : ProjectStartedReceiver {

	override suspend fun receiveProjectStarted(projectStarted: StartNewProject.ResponseModel) {}

}