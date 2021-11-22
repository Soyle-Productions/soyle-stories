package com.soyle.stories.di.layout

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.layout.closeTool.CloseToolController
import com.soyle.stories.layout.closeTool.CloseToolControllerImpl
import com.soyle.stories.layout.config.defaultLayout
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.layout.openTool.OpenToolControllerImpl
import com.soyle.stories.layout.openTool.OpenToolNotifier
import com.soyle.stories.layout.removeToolsWithId.RemoveToolsWithIdController
import com.soyle.stories.layout.removeToolsWithId.RemoveToolsWithIdNotifier
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.closeTool.CloseToolUseCase
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayoutUseCase
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.layout.usecases.openTool.OpenToolUseCase
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithId
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithIdUseCase
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpenedUseCase
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBenchModel
import com.soyle.stories.project.eventbus.CloseToolNotifier
import com.soyle.stories.project.eventbus.GetSavedLayoutNotifier
import com.soyle.stories.project.eventbus.ToggleToolOpenedNotifier
import com.soyle.stories.project.layout.LayoutController
import com.soyle.stories.project.layout.LayoutPresenter
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.scene.delete.SceneDeletedNotifier
import com.soyle.stories.theme.deleteTheme.ThemeDeletedNotifier

object LayoutModule {

    init {
        scoped<ProjectScope> {

            run {
                provide<GetSavedLayout> {
                    GetSavedLayoutUseCase(get(), ::defaultLayout)
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
                provide<RemoveToolsWithId> {
                    RemoveToolsWithIdUseCase(projectId, get())
                }
            }

            run {
                provide(GetSavedLayout.OutputPort::class) {
                    GetSavedLayoutNotifier(applicationScope.get())
                }
                provide(ToggleToolOpened.OutputPort::class) {
                    ToggleToolOpenedNotifier(applicationScope.get())
                }
                provide(OpenTool.OutputPort::class) {
                    OpenToolNotifier(applicationScope.get())
                }
                provide(CloseTool.OutputPort::class) {
                    CloseToolNotifier(applicationScope.get())
                }
                provide(RemoveToolsWithId.OutputPort::class) {
                    RemoveToolsWithIdNotifier(applicationScope.get())
                }
            }

            provide(OpenToolController::class) {
                OpenToolControllerImpl(applicationScope.get(), applicationScope.get(), get(), get())
            }
            provide(CloseToolController::class) {
                CloseToolControllerImpl(applicationScope.get(), get(), get())
            }
            provide {
                RemoveToolsWithIdController(get(), get()).also {
                    get<SceneDeletedNotifier>().addListener(it)
                    get<ThemeDeletedNotifier>().addListener(it)
                }
            }

            provide<LayoutViewListener> {
                get<RemoveToolsWithIdController>()
                LayoutController(
                    applicationScope.get(),
                    get(),
                    get(),
                    get(),
                    get(),
                    get(),
                    get(),
                    LayoutPresenter(
                        get<WorkBenchModel>(),
                        get<GetSavedLayoutNotifier>(),
                        get<ToggleToolOpenedNotifier>(),
                        get<OpenToolNotifier>(),
                        get<CloseToolNotifier>(),
                        get<RemoveToolsWithIdNotifier>(),
                        ToolModule
                    )
                )
            }

        }
    }

}