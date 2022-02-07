package com.soyle.stories.desktop.view

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.view.project.close.CloseProjectPromptViewModel
import com.soyle.stories.desktop.view.project.start.StartProjectPromptViewModel
import com.soyle.stories.project.closeProject.*
import com.soyle.stories.project.list.ListOpenProjectsController
import com.soyle.stories.project.openProject.*
import com.soyle.stories.project.startNewProject.*
import com.soyle.stories.repositories.ProjectFileRepository
import com.soyle.stories.repositories.WorkspaceRepositoryImpl
import com.soyle.stories.stores.ProjectFileStore
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.project.startNewProject.StartNewProject
import com.soyle.stories.usecase.project.startNewProject.StartNewProjectUseCase
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.closeProject.CloseProjectUseCase
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjectsUseCase
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.openProject.OpenProjectUseCase
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.UUID.randomUUID
import kotlin.coroutines.CoroutineContext
import com.soyle.stories.workspace.repositories.ProjectRepository as WorkspaceProjectRepository

val applicationModule = module {
    single<CoroutineContext>(named("main context"), createdAtStart = true) { Dispatchers.Main }
    single<CoroutineContext>(named("io context"), createdAtStart = true) { Dispatchers.IO }
    single<CoroutineContext>(named("async context"), createdAtStart = true) { Dispatchers.Default }

    single<ThreadTransformer> { ThreadTransfomerImpl() }
}

val workspaceModules = listOf(
    module {
        single<WorkspaceRepository> { WorkspaceRepositoryImpl() }
        single<WorkspaceProjectRepository> { get<ProjectFileRepository>() }
    }
)

val projectModules = listOf(
    module {
        single { StartProjectPromptViewModel() }
        single<StartProjectPrompt> { get<StartProjectPromptViewModel>() }

        single { CloseProjectPromptViewModel() }
        single<CloseProjectPrompt> { get<CloseProjectPromptViewModel>() }
    },
    module {
        single { ProjectStartedNotifier() }
        single<ProjectStartedReceiver> { get<ProjectStartedNotifier>() }

        single { ProjectOpenedNotifier() }
        single<ProjectOpenedReceiver> { get<ProjectOpenedNotifier>() }

        single { ClosedProjectNotifier() }
        single<ClosedProjectReceiver> { get<ClosedProjectNotifier>() }

        single { CloseProjectRequestNotifier() }
        single<CloseProjectRequestReceiver> { get<CloseProjectRequestNotifier>() }
    },
    module {
        single<ListOpenProjectsController> {
            ListOpenProjectsController.Implementation(
                get(named("main context")),
                get(named("async context")),
                get()
            )
        }

        single<CloseProject.OutputPort> { CloseProjectOutput(get(), get(), get()) }
        single<CloseProjectController> {
            CloseProjectController.Implementation(
                get(named("main context")),
                get(named("async context")),
                get(),
                get(),
                get()
            )
        }

        single<OpenProjectController> { OpenProjectControllerImpl(get(), get(), get()) }
        single<OpenProject.OutputPort> { OpenProjectOutput(get(), get()) }

        single<StartProjectController> {
            StartProjectController.Implementaton(
                get(named("main context")),
                get(named("io context")),
                get(named("async context")),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
        single<StartNewProject.OutputPort> { StartNewProjectOutput(get(), get()) }
    },
    module {
        single<ProjectRepository> { get<ProjectFileRepository>() }
        single { ProjectFileRepository(ProjectFileStore()) }
        single<FileRepository> { get<ProjectFileRepository>() }
    },
    module {
        val workerId = randomUUID().toString()

        single<CloseProject> { CloseProjectUseCase(workerId, get()) }
        single<OpenProject> { OpenProjectUseCase(workerId, get(), get(), get()) }
        single<ListOpenProjects> { ListOpenProjectsUseCase(workerId, get(), get()) }
        single<StartNewProject> { StartNewProjectUseCase(get()) }
    }
)

fun main() {
    startKoin {
        modules(projectModules)
        modules(workspaceModules)
        modules(applicationModule)
    }
    SoyleStories()
}