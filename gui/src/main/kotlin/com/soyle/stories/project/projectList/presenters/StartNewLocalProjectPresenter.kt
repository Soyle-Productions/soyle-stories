package com.soyle.stories.project.projectList.presenters

import com.soyle.stories.project.*
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.StartProjectFailureViewModel
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject

internal class StartNewLocalProjectPresenter(
  private val view: ProjectListView,
  private val openProjectOutputPort: OpenProject.OutputPort,
  private val closeProjectOutputPort: CloseProject.OutputPort
) : StartNewLocalProject.OutputPort {

	override fun receiveOpenProjectFailure(failure: ProjectException) {
		openProjectOutputPort.receiveOpenProjectFailure(failure)
	}

	override fun receiveOpenProjectResponse(response: OpenProject.ResponseModel) {
		openProjectOutputPort.receiveOpenProjectResponse(response)
	}

	override fun receiveCloseProjectFailure(failure: Exception) {
		closeProjectOutputPort.receiveCloseProjectFailure(failure)
	}

	override fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
		closeProjectOutputPort.receiveCloseProjectResponse(response)
	}

	override fun receiveStartNewLocalProjectFailure(exception: LocalProjectException) {
		view.updateOrInvalidated {
			copy(
              startProjectFailure = StartProjectFailureViewModel(
				failingField = when (exception) {
					is DirectoryDoesNotExist -> "directory"
					is ProjectFailure -> when (exception.cause) {
						is NameCannotBeBlank -> "name"
						else -> null
					}
					else -> null
				},
				message = when (exception) {
					is DirectoryDoesNotExist -> "Directory does not exist"
					is FileAlreadyExists -> "File already exists"
					is ProjectFailure -> when (exception.cause) {
						is NameCannotBeBlank -> "Project name cannot be blank"
						else -> exception.localizedMessage
					}
					else -> exception.localizedMessage ?: ""
				}
			  )
			)
		}
	}

}