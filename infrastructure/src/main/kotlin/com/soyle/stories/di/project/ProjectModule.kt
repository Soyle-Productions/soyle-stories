package com.soyle.stories.di.project

import com.soyle.stories.di.DI
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.modules.DataModule
import com.soyle.stories.di.scoped
import com.soyle.stories.common.Notifier
import com.soyle.stories.project.eventbus.OpenProjectNotifier
import com.soyle.stories.project.eventbus.ProjectEvents
import com.soyle.stories.project.eventbus.RequestCloseProjectNotifier
import com.soyle.stories.project.eventbus.StartNewProjectNotifier
import com.soyle.stories.project.projectList.ProjectListController
import com.soyle.stories.project.projectList.ProjectListPresenter
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.project.usecases.startNewProject.StartNewProjectUseCase
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProjectUseCase
import com.soyle.stories.soylestories.ApplicationModel
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.confirmExitDialog.*
import com.soyle.stories.soylestories.welcomeScreen.*
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.closeProject.CloseProjectUseCase
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjectsUseCase
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.openProject.OpenProjectUseCase
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProjectUseCase
import tornadofx.find

object ProjectModule {

	private fun InScope<ApplicationScope>.useCases() {
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
	}

	private fun InScope<ApplicationScope>.events() {

		provide(RequestCloseProject.OutputPort::class) {
			RequestCloseProjectNotifier()
		}
		provide(OpenProject.OutputPort::class) {
			OpenProjectNotifier(get())
		}
		provide(StartNewLocalProject.OutputPort::class) {
			StartNewProjectNotifier(get())
		}

		provide<ProjectEvents> {
			val scope = this
			object : ProjectEvents {
				override val closeProject: Notifier<RequestCloseProject.OutputPort> by DI.resolveLater<RequestCloseProjectNotifier>(scope)
				override val openProject: Notifier<OpenProject.OutputPort> by DI.resolveLater<OpenProjectNotifier>(scope)
				override val startNewProject: Notifier<StartNewLocalProject.OutputPort> by DI.resolveLater<StartNewProjectNotifier>(scope)
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
			ProjectListController(
			  get(),
			  ProjectListPresenter(
				get(),
				get()
			  ),
			  get(),
			  get(),
			  get(),
			  StartNewLocalProjectUseCase(
				get(),
				StartNewProjectUseCase(
				  get()
				),
				get()
			  ),
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

		scoped<ApplicationScope> {

			useCases()
			events()
			viewListeners()
			views()

		}

	}

}