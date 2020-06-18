package com.soyle.stories.di.theme

import com.soyle.stories.common.listensTo
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.createTheme.CreateThemeController
import com.soyle.stories.theme.createTheme.CreateThemeControllerImpl
import com.soyle.stories.theme.createTheme.CreateThemeNotifier
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogController
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogModel
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogPresenter
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogViewListener
import com.soyle.stories.theme.themeList.ThemeListController
import com.soyle.stories.theme.themeList.ThemeListModel
import com.soyle.stories.theme.themeList.ThemeListPresenter
import com.soyle.stories.theme.themeList.ThemeListViewListener
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreateThemeUseCase
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByThemeUseCase

object ThemeModule {

    init {

        scoped<ProjectScope> {
            usecases()
            notifiers()
            controllers()
            gui()
        }

    }

    private fun InScope<ProjectScope>.usecases()
    {
        provide<CreateTheme> { CreateThemeUseCase(get()) }
        provide<ListSymbolsByTheme> { ListSymbolsByThemeUseCase(get()) }
    }

    private fun InScope<ProjectScope>.notifiers()
    {
        provide(CreateTheme.OutputPort::class) {
            CreateThemeNotifier()
        }
    }

    private fun InScope<ProjectScope>.controllers()
    {
        provide<CreateThemeController> {
            CreateThemeControllerImpl(projectId.toString(), applicationScope.get(), get(), get())
        }
    }

    private fun InScope<ProjectScope>.gui()
    {
        provide<CreateThemeDialogViewListener> {
            val presenter = CreateThemeDialogPresenter(
                get<CreateThemeDialogModel>()
            )

            presenter listensTo get<CreateThemeNotifier>()

            CreateThemeDialogController(
                presenter,
                get()
            )
        }

        provide<ThemeListViewListener> {
            val presenter = ThemeListPresenter(
                get<ThemeListModel>()
            )

            presenter listensTo get<CreateThemeNotifier>()

            ThemeListController(
                projectId.toString(),
                applicationScope.get(),
                get(),
                presenter
            )
        }
    }
}