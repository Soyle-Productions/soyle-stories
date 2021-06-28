package com.soyle.stories.di.project

import com.soyle.stories.di.get
import com.soyle.stories.di.modules.DataModule
import com.soyle.stories.di.scoped
import com.soyle.stories.project.closeProject.CloseProjectOutput
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.closeProject.CloseProjectUseCase
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjectsUseCase
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.openProject.OpenProjectUseCase
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProjectUseCase

object ProjectUseCases {

    init {
        scoped<ApplicationScope> {
            provide<ListOpenProjects> {
                ListOpenProjectsUseCase(DataModule.workerId, get(), get())
            }
            provide<RequestCloseProject> {
                RequestCloseProjectUseCase(DataModule.workerId, get(), get())
            }
            provide<CloseProject> {
                CloseProjectUseCase(DataModule.workerId, get())
            }
            provide<OpenProject> {
                OpenProjectUseCase(DataModule.workerId, get(), get(), get())
            }

            provide(
                CloseProject.OutputPort::class,
                RequestCloseProject.OutputPort::class
            ) {
                CloseProjectOutput(get(), get(), get())
            }
        }
    }

}