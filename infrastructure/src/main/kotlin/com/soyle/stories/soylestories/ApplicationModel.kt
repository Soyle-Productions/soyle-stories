package com.soyle.stories.soylestories

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.di.DI
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.ProjectListViewModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 3:24 PM
 */
class ApplicationModel : Model<ApplicationScope, ProjectListViewModel>(ApplicationScope::class),
  ProjectListView {

	override val applicationScope: ApplicationScope
		get() = scope

	val isInvalidated = SimpleBooleanProperty(true)
	val initializationMessage = SimpleStringProperty("")
	val initializationProgress = SimpleDoubleProperty(0.0)
	val isSplashScreenVisible = SimpleBooleanProperty(true)
	val openProjectRequest = SimpleObjectProperty<ProjectFileViewModel>()
	val isOpenProjectOptionsDialogOpen = bind(ProjectListViewModel::isOpenProjectOptionsDialogOpen)
	val isWelcomeScreenVisible = bind(ProjectListViewModel::isWelcomeScreenVisible)
	val openProjects = bind(ProjectListViewModel::openProjects)
	val isFailedProjectDialogVisible = bind(ProjectListViewModel::isFailedProjectDialogVisible)
	val failedProjects = bind(ProjectListViewModel::failedProjects)
	val startProjectFailure = bind(ProjectListViewModel::startProjectFailure)

	val closingProject = bind(ProjectListViewModel::closeProjectRequest)

	override fun update(update: ProjectListViewModel?.() -> ProjectListViewModel) {
		threadTransformer.gui {
			val currentItem = item?.let { viewModel() }
			val nextItem = currentItem.update()
			isSplashScreenVisible.set(nextItem.isSplashScreenVisible)
			openProjectRequest.set(nextItem.openProjectRequest)
			/*
			VERY difficult to debug issue.  If openProjectRequest property is bound to the item or these above properties
			are set after item is updated, then isOpenProjectOptionsDialogOpen property is set before openProjectRequest
			property.  This means, the openProjectOptionDialog will open and block the thread, meaning openProjectRequest is
			never set until after the dialog is closed.  This makes the openProjectRequest property value equal null, making
			it fail.
			 */
			item = nextItem
		}
	}

	override fun updateOrInvalidated(update: ProjectListViewModel.() -> ProjectListViewModel) {
		threadTransformer.gui {
			val currentItem = item?.let { viewModel() } ?: return@gui invalidate()
			val nextItem = currentItem.update()
			isSplashScreenVisible.set(nextItem.isSplashScreenVisible)
			openProjectRequest.set(nextItem.openProjectRequest)
			/*
			VERY difficult to debug issue.  If openProjectRequest property is bound to the item or these above properties
			are set after item is updated, then isOpenProjectOptionsDialogOpen property is set before openProjectRequest
			property.  This means, the openProjectOptionDialog will open and block the thread, meaning openProjectRequest is
			never set until after the dialog is closed.  This makes the openProjectRequest property value equal null, making
			it fail.
			 */
			item = nextItem
		}
	}

	fun invalidate() {
		isInvalidated.set(true)
	}

	companion object {
		const val MAX_LOADING_VALUE = 1.0
	}
}