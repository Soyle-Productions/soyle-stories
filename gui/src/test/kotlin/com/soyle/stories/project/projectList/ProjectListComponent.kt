package com.soyle.stories.project.projectList

import com.soyle.stories.project.usecases.startNewProject.StartNewProjectUseCase
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProjectUseCase
import com.soyle.stories.soylestories.DataComponent
import com.soyle.stories.soylestories.SoyleStoriesComponent

class ProjectListComponent(
  private val application: SoyleStoriesComponent,
  private val dataComponent: DataComponent,
  private val projectListView: ProjectListView
) {

	val projectListViewListener: ProjectListViewListener by lazy {
		ProjectListController(
		  application.listOpenProjects,
		  ProjectListPresenter(
			projectListView,
			application.projectEvents
		  ),
		  application.closeProject,
		  application.requestCloseProject,
		  application.requestCloseProjectOutputPort,
		  StartNewLocalProjectUseCase(
			dataComponent.fileRepository,
			StartNewProjectUseCase(
			  dataComponent.projectRepository
			),
			application.openProject
		  ),
		  application.startNewProjectOutputPort,
		  application.openProject,
		  application.openProjectOutputPort
		)
	}

}