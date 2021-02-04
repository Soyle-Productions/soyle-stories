package com.soyle.stories.project.projectList.presenters

import com.soyle.stories.project.*
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.StartProjectFailureViewModel
import com.soyle.stories.project.startNewProject.ProjectStartedReceiver
import com.soyle.stories.project.usecases.startNewProject.StartNewProject
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject

internal class StartNewLocalProjectPresenter(
) : ProjectStartedReceiver {

	override suspend fun receiveProjectStarted(projectStarted: StartNewProject.ResponseModel) {}

}