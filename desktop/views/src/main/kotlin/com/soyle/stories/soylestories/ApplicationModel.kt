package com.soyle.stories.soylestories

import com.soyle.stories.common.Model
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.ProjectListViewModel
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 3:24 PM
 */
class ApplicationModel : Model<ApplicationScope, ProjectListViewModel>(ApplicationScope::class),
  ProjectListView {

	override val applicationScope: ApplicationScope
		get() = scope

	val initializationMessage = SimpleStringProperty("")
	val initializationProgress = SimpleDoubleProperty(0.0)
	val isSplashScreenVisible = bind(ProjectListViewModel::isSplashScreenVisible)
	val openProjectRequest = bind(ProjectListViewModel::openProjectRequest)
	val isOpenProjectOptionsDialogOpen = bind(ProjectListViewModel::isOpenProjectOptionsDialogOpen)
	val isWelcomeScreenVisible = bind(ProjectListViewModel::isWelcomeScreenVisible)
	val openProjects = bind(ProjectListViewModel::openProjects)
	val isFailedProjectDialogVisible = bind(ProjectListViewModel::isFailedProjectDialogVisible)
	val failedProjects = bind(ProjectListViewModel::failedProjects)
	val startProjectFailure = bind(ProjectListViewModel::startProjectFailure)

	val closingProject = bind(ProjectListViewModel::closeProjectRequest)

	companion object {
		const val MAX_LOADING_VALUE = 1.0
	}
}