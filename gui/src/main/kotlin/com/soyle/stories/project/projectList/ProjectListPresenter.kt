package com.soyle.stories.project.projectList

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.project.eventbus.ProjectEvents
import com.soyle.stories.project.projectList.presenters.CloseProjectPresenter
import com.soyle.stories.project.projectList.presenters.OpenProjectPresenter
import com.soyle.stories.project.projectList.presenters.StartNewLocalProjectPresenter
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.workspace.ExpectedProjectDoesNotExistAtLocation
import com.soyle.stories.workspace.ExpectedProjectException
import com.soyle.stories.workspace.UnexpectedProjectExistsAtLocation
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject

class ProjectListPresenter(
  private val view: ProjectListView,
  projectEvents: ProjectEvents
) : ListOpenProjects.OutputPort {

	// the sub presenters should not be GC'd until the project list presenter is GC'd, but notifiers hold weak references
	// to their listeners, so we have to hold strong references here to make sure the sub presenters are not GC'd
	// prematurely.
	private val subPresenters: List<Any>

	init {
		val closeProjectOutputPort: RequestCloseProject.OutputPort = CloseProjectPresenter(view)
		val openProjectOutputPort: OpenProject.OutputPort = OpenProjectPresenter(view, closeProjectOutputPort)

		val startNewLocalProjectOutputPort: StartNewLocalProject.OutputPort =
		  StartNewLocalProjectPresenter(view, openProjectOutputPort, closeProjectOutputPort)

		subPresenters = listOf(
		  startNewLocalProjectOutputPort listensTo projectEvents.openProject,
		  closeProjectOutputPort listensTo projectEvents.closeProject,
		  startNewLocalProjectOutputPort listensTo projectEvents.startNewProject
		)
	}

	override fun receiveListOpenProjectsResponse(response: ListOpenProjects.ResponseModel) {
		view.update {
			updateViewModelOrDefault(this, response)
		}
	}

	private fun updateViewModelOrDefault(viewModel: ProjectListViewModel?, response: ListOpenProjects.ResponseModel): ProjectListViewModel {
		return if (viewModel != null) {
			updateViewModelWithResponse(viewModel, response)
		} else {
			defaultProjectListModel(response)
		}
	}

	private fun updateViewModelWithResponse(viewModel: ProjectListViewModel, response: ListOpenProjects.ResponseModel): ProjectListViewModel {
		return viewModel.copy(
		  isSplashScreenVisible = false,
		  isWelcomeScreenVisible = response.openProjects.isEmpty() && response.failedProjects.isEmpty(),
		  openProjects = response.openProjects.map(::convertOpenProjectItemToViewModel),
		  failedProjects = response.failedProjects.map(::convertExpectedProjectExceptionToViewModel),
		  startProjectFailure = null
		)
	}

	private fun defaultProjectListModel(response: ListOpenProjects.ResponseModel) = ProjectListViewModel(
	  openProjectRequest = null,
	  isSplashScreenVisible = false,
	  isWelcomeScreenVisible = response.openProjects.isEmpty() && response.failedProjects.isEmpty(),
	  openProjects = response.openProjects.map(::convertOpenProjectItemToViewModel),
	  failedProjects = response.failedProjects.map(::convertExpectedProjectExceptionToViewModel),
	  closeProjectRequest = null,
	  startProjectFailure = null
	)

	private fun convertExpectedProjectExceptionToViewModel(it: ExpectedProjectException): ProjectIssueViewModel {
		return when (it) {
			is ExpectedProjectDoesNotExistAtLocation -> ProjectIssueViewModel(
			  it.expectedProjectName,
			  it.location
			)
			is UnexpectedProjectExistsAtLocation -> ProjectIssueViewModel(
			  it.expectedProjectName,
			  it.location,
			  "Found unexpected project: ${it.foundProjectName}"
			)
		}
	}

	private fun convertOpenProjectItemToViewModel(item: ListOpenProjects.OpenProjectItem): ProjectFileViewModel {
		return ProjectFileViewModel(
		  item.projectId,
		  item.projectName,
		  item.projectLocation
		)
	}
}