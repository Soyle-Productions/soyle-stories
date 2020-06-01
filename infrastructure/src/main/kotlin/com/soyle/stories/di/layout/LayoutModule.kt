package com.soyle.stories.di.layout

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.layout.openTool.OpenToolControllerImpl
import com.soyle.stories.layout.openTool.OpenToolNotifier
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.closeTool.CloseToolUseCase
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayoutUseCase
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.layout.usecases.openTool.OpenToolUseCase
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpenedUseCase
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBenchModel
import com.soyle.stories.project.eventbus.CloseToolNotifier
import com.soyle.stories.project.eventbus.GetSavedLayoutNotifier
import com.soyle.stories.project.eventbus.ToggleToolOpenedNotifier
import com.soyle.stories.project.layout.LayoutController
import com.soyle.stories.project.layout.LayoutPresenter
import com.soyle.stories.project.layout.LayoutView
import com.soyle.stories.project.layout.LayoutViewListener
import tornadofx.find

object LayoutModule {

	init {
		scoped<ProjectScope> {

			usecases@ run {
				provide<GetSavedLayout> {
					GetSavedLayoutUseCase(get())
				}
				provide<ToggleToolOpened> {
					ToggleToolOpenedUseCase(projectId, get())
				}
				provide<OpenTool> {
					OpenToolUseCase(projectId, get(), get())
				}
				provide<CloseTool> {
					CloseToolUseCase(projectId, get())
				}
			}

			events@ run {
				provide(GetSavedLayout.OutputPort::class) {
					GetSavedLayoutNotifier()
				}
				provide(ToggleToolOpened.OutputPort::class) {
					ToggleToolOpenedNotifier()
				}
				provide(OpenTool.OutputPort::class) {
					OpenToolNotifier()
				}
				provide(CloseTool.OutputPort::class) {
					CloseToolNotifier()
				}
			}

			provide(OpenToolController::class) {
				OpenToolControllerImpl(applicationScope.get(), get(), get())
			}

			provide<LayoutViewListener> {
				LayoutController(
				  applicationScope.get(),
				  get(),
				  get(),
				  get(),
				  get(),
				  get(),
				  get(),
				  LayoutPresenter(
					get(),
					applicationScope.get(),
					get<GetSavedLayoutNotifier>(),
					get<ToggleToolOpenedNotifier>(),
					get<OpenToolNotifier>(),
					get<CloseToolNotifier>()
				  )
				)
			}

			provide<LayoutView> {
				find<WorkBenchModel>(scope = this)
			}

		}
	}

}