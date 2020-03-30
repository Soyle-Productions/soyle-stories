package com.soyle.stories.soylestories

import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.ProjectListViewModel
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.rebind
import tornadofx.runLater

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 3:24 PM
 */
class ApplicationModel :
    ItemViewModel<ProjectListViewModel>(),
    ProjectListView {

    val isInvalidated = SimpleBooleanProperty(true)
    val initializationMessage = SimpleStringProperty("")
    val initializationProgress = SimpleDoubleProperty(0.0)
    val isSplashScreenVisible = SimpleBooleanProperty(true)
    val openProjectRequest = bind(ProjectListViewModel::openProjectRequest)
    val isOpenProjectOptionsDialogOpen = bind(ProjectListViewModel::isOpenProjectOptionsDialogOpen)
    val isWelcomeScreenVisible = bind(ProjectListViewModel::isWelcomeScreenVisible)
    val openProjects = bindImmutableList(ProjectListViewModel::openProjects)
    val isFailedProjectDialogVisible = bind(ProjectListViewModel::isFailedProjectDialogVisible)
    val failedProjects = bindImmutableList(ProjectListViewModel::failedProjects)

    val closingProject = bind(ProjectListViewModel::closeProjectRequest)

    private fun viewModel() = ProjectListViewModel(
        isSplashScreenVisible.value,
        isWelcomeScreenVisible.value,
        openProjectRequest.value,
        openProjects.value,
        failedProjects.value,
        closingProject.value
    )

    override fun update(update: ProjectListViewModel?.() -> ProjectListViewModel) {
        if (!Platform.isFxApplicationThread()) return runLater { update(update) }
        val newItem = item?.let { viewModel() }
        rebind { item = newItem.update() }
        isSplashScreenVisible.set(this.item!!.isSplashScreenVisible)
    }

    override fun updateOrInvalidated(update: ProjectListViewModel.() -> ProjectListViewModel) {
        if (!Platform.isFxApplicationThread()) return runLater { updateOrInvalidated(update) }
        val currentItem = item ?: return invalidate()
        rebind { item = currentItem.update() }
    }

    fun invalidate() {
        isInvalidated.set(true)
    }

    companion object {
        const val MAX_LOADING_VALUE = 1.0
    }
}