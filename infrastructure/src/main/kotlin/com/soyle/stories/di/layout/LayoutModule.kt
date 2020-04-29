package com.soyle.stories.di.layout

import com.soyle.stories.characterarc.eventbus.DeleteLocalCharacterArcNotifier
import com.soyle.stories.characterarc.eventbus.RemoveCharacterFromLocalComparisonNotifier
import com.soyle.stories.characterarc.eventbus.RemoveCharacterFromLocalStoryNotifier
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.Project
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
import com.soyle.stories.project.layout.openTool.OpenToolController
import com.soyle.stories.project.layout.openTool.OpenToolControllerImpl
import com.soyle.stories.project.layout.openTool.OpenToolNotifier
import tornadofx.find

object LayoutModule {

	init {
		scoped<ProjectScope> {

			usecases@ run {
				provide<GetSavedLayout> {
					GetSavedLayoutUseCase(get())
				}
				provide<ToggleToolOpened> {
					ToggleToolOpenedUseCase(get())
				}
				provide<OpenTool> {
					OpenToolUseCase(Project.Id(projectId), get())
				}
				provide<CloseTool> {
					CloseToolUseCase(get(), projectId)
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
					get<GetSavedLayoutNotifier>(),
					get<ToggleToolOpenedNotifier>(),
					get<OpenToolNotifier>(),
					get<CloseToolNotifier>(),
					get<RemoveCharacterFromLocalStoryNotifier>(),
					get<DeleteLocalCharacterArcNotifier>(),
					get<RemoveCharacterFromLocalComparisonNotifier>(),
					get()
				  )
				)
			}

			provide<LayoutView> {
				find<WorkBenchModel>(scope = this)
			}

		}
	}

}