package com.soyle.stories.di.project

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.DI
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.closeProject.ClosedProjectNotifier
import com.soyle.stories.project.closeProject.ClosedProjectReceiver
import com.soyle.stories.project.eventbus.OpenProjectNotifier
import com.soyle.stories.project.eventbus.ProjectEvents
import com.soyle.stories.project.eventbus.RequestCloseProjectNotifier
import com.soyle.stories.project.eventbus.StartNewProjectNotifier
import com.soyle.stories.project.openProject.*
import com.soyle.stories.project.projectList.ProjectListController
import com.soyle.stories.project.projectList.ProjectListPresenter
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.project.startNewProject.*
import com.soyle.stories.project.usecases.startNewProject.StartNewProject
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.soylestories.ApplicationModel
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.confirmExitDialog.*
import com.soyle.stories.soylestories.welcomeScreen.*
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject
import tornadofx.find

object ProjectModule {

    private fun InScope<ApplicationScope>.controllers() {
        provide<OpenProjectController> {
            OpenProjectControllerImpl(get(), get(), get())
        }
        provide<StartProjectController> {
            StartProjectControllerImpl(get(), get(), get(), get())
        }
    }

    private fun InScope<ApplicationScope>.events() {

        provide(RequestCloseProject.OutputPort::class) {
            RequestCloseProjectNotifier(get())
        }
        provide(OpenProject.OutputPort::class) {
            OpenProjectOutput(get(), get())
        }

        provide(ProjectStartedReceiver::class) {
            ProjectStartedNotifier()
        }
        provide(ProjectOpenedReceiver::class) {
            ProjectOpenedNotifier()
        }
        provide(ClosedProjectReceiver::class) {
            ClosedProjectNotifier()
        }
        provide(StartNewProject.OutputPort::class) {
            StartNewProjectOutput(
                get(),
                get()
            )
        }

        provide<ProjectEvents> {
            val scope = this
            object : ProjectEvents {
                override val closeProject: Notifier<RequestCloseProject.OutputPort> by DI.resolveLater<RequestCloseProjectNotifier>(
                    scope
                )
                override val openProject: Notifier<OpenProject.OutputPort> by DI.resolveLater<OpenProjectNotifier>(scope)
                override val startNewProject: Notifier<StartNewLocalProject.OutputPort> by DI.resolveLater<StartNewProjectNotifier>(
                    scope
                )
            }
        }
    }

    private fun InScope<ApplicationScope>.viewListeners() {
        provide<WelcomeScreenViewListener> {
            WelcomeScreenController(
                WelcomeScreenPresenter(get())
            )
        }
        provide<ConfirmExitDialogViewListener> {
            ConfirmExitDialogController(
                ConfirmExitDialogPresenter(get())
            )
        }

        provide<ProjectListViewListener> {
            val presenter = ProjectListPresenter(
                get()
            )

            presenter listensTo get<ProjectOpenedNotifier>()
            presenter listensTo get<ClosedProjectNotifier>()

            ProjectListController(
                get(),
                presenter,
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

    private fun InScope<ApplicationScope>.views() {
        provide<WelcomeScreenView> { find<WelcomeScreenModel>(this) }
        provide<ConfirmExitDialogView> { find<ConfirmExitDialogModel>(this) }
        provide<ProjectListView> { find<ApplicationModel>(this) }
    }

    init {

        ProjectUseCases

        scoped<ApplicationScope> {
            controllers()
            events()
            viewListeners()
            views()

        }

    }

}