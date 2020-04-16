package com.soyle.stories.soylestories

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.project.eventbus.OpenProjectNotifier
import com.soyle.stories.project.eventbus.ProjectEvents
import com.soyle.stories.project.eventbus.RequestCloseProjectNotifier
import com.soyle.stories.project.eventbus.StartNewProjectNotifier
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.closeProject.CloseProjectUseCase
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjectsUseCase
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.openProject.OpenProjectUseCase
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProjectUseCase

class SoyleStoriesComponent(
  private val dataComponent: DataComponent
) {

	val listOpenProjects: ListOpenProjects by lazy {
		ListOpenProjectsUseCase(
		  dataComponent.workerId,
		  dataComponent.workspaceRepository,
		  dataComponent.projectFileRepository
		)
	}

	val closeProject: CloseProject by lazy {
		CloseProjectUseCase(dataComponent.workerId, dataComponent.workspaceRepository)
	}
	val requestCloseProject: RequestCloseProject by lazy {
		RequestCloseProjectUseCase(dataComponent.workerId, closeProject, dataComponent.workspaceRepository)
	}
	val openProject: OpenProject by lazy {
		OpenProjectUseCase(
		  dataComponent.workerId,
		  dataComponent.workspaceRepository,
		  dataComponent.projectFileRepository,
		  closeProject
		)
	}

	private val requestCloseProjectNotifier by lazy {
		RequestCloseProjectNotifier()
	}
	private val openProjectNotifier by lazy {
		OpenProjectNotifier(requestCloseProjectNotifier)
	}
	private val startNewProjectNotifier: StartNewProjectNotifier by lazy {
		StartNewProjectNotifier(openProjectNotifier)
	}

	val projectEvents = object : ProjectEvents {
		override val closeProject: Notifier<RequestCloseProject.OutputPort>
			get() = requestCloseProjectNotifier
		override val openProject: Notifier<OpenProject.OutputPort>
			get() = openProjectNotifier
		override val startNewProject: Notifier<StartNewLocalProject.OutputPort>
			get() = startNewProjectNotifier
	}

	val requestCloseProjectOutputPort: RequestCloseProject.OutputPort
		get() = requestCloseProjectNotifier

	val openProjectOutputPort: OpenProject.OutputPort
		get() = openProjectNotifier

	val startNewProjectOutputPort: StartNewLocalProject.OutputPort
		get() = startNewProjectNotifier

}