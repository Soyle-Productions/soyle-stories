package com.soyle.stories.di.theme

import com.soyle.stories.common.listensTo
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeController
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeControllerImpl
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeNotifier
import com.soyle.stories.theme.createSymbolDialog.*
import com.soyle.stories.theme.createTheme.CreateThemeController
import com.soyle.stories.theme.createTheme.CreateThemeControllerImpl
import com.soyle.stories.theme.createTheme.CreateThemeNotifier
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogController
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogModel
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogPresenter
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogViewListener
import com.soyle.stories.theme.deleteTheme.DeleteThemeController
import com.soyle.stories.theme.deleteTheme.DeleteThemeControllerImpl
import com.soyle.stories.theme.deleteTheme.DeleteThemeNotifier
import com.soyle.stories.theme.deleteThemeDialog.*
import com.soyle.stories.theme.renameTheme.RenameThemeController
import com.soyle.stories.theme.renameTheme.RenameThemeControllerImpl
import com.soyle.stories.theme.renameTheme.RenameThemeNotifier
import com.soyle.stories.theme.themeList.ThemeListController
import com.soyle.stories.theme.themeList.ThemeListModel
import com.soyle.stories.theme.themeList.ThemeListPresenter
import com.soyle.stories.theme.themeList.ThemeListViewListener
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsModel
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToThemeUseCase
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreateThemeUseCase
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeleteThemeUseCase
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByThemeUseCase
import com.soyle.stories.theme.usecases.listThemes.ListThemes
import com.soyle.stories.theme.usecases.listThemes.ListThemesUseCase
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ListValueWebsInTheme
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ListValueWebsInThemeUseCase
import com.soyle.stories.theme.usecases.renameTheme.RenameTheme
import com.soyle.stories.theme.usecases.renameTheme.RenameThemeUseCase
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsController
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsPresenter
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewListener

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
        provide<DeleteTheme> { DeleteThemeUseCase(get()) }
        provide<RenameTheme> { RenameThemeUseCase(get()) }
        provide<AddSymbolToTheme> { AddSymbolToThemeUseCase(get()) }
        provide<ListThemes> { ListThemesUseCase(get()) }
        provide<ListValueWebsInTheme> { ListValueWebsInThemeUseCase(get()) }
    }

    private fun InScope<ProjectScope>.notifiers()
    {
        provide(CreateTheme.OutputPort::class) {
            CreateThemeNotifier(get())
        }
        provide(DeleteTheme.OutputPort::class) {
            DeleteThemeNotifier()
        }
        provide(RenameTheme.OutputPort::class) {
            RenameThemeNotifier()
        }
        provide(AddSymbolToTheme.OutputPort::class) {
            AddSymbolToThemeNotifier()
        }
    }

    private fun InScope<ProjectScope>.controllers()
    {
        provide<CreateThemeController> {
            CreateThemeControllerImpl(projectId.toString(), applicationScope.get(), get(), get())
        }
        provide<DeleteThemeController> {
            DeleteThemeControllerImpl(applicationScope.get(), get(), get())
        }
        provide<RenameThemeController> {
            RenameThemeControllerImpl(applicationScope.get(), get(), get())
        }
        provide<AddSymbolToThemeController> {
            AddSymbolToThemeControllerImpl(applicationScope.get(), get(), get())
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

        provide<CreateSymbolDialogViewListener> {
            val presenter = CreateSymbolDialogPresenter(
                get<CreateSymbolDialogModel>()
            )

            presenter listensTo get<AddSymbolToThemeNotifier>()
            presenter listensTo get<CreateThemeNotifier>()
            presenter listensTo get<DeleteThemeNotifier>()
            presenter listensTo get<RenameThemeNotifier>()

            CreateSymbolDialogController(
                projectId.toString(),
                applicationScope.get(),
                presenter,
                get(),
                get(),
                get()
            )
        }

        provide<ThemeListViewListener> {
            val presenter = ThemeListPresenter(
                get<ThemeListModel>()
            )

            presenter listensTo get<CreateThemeNotifier>()
            presenter listensTo get<DeleteThemeNotifier>()
            presenter listensTo get<RenameThemeNotifier>()
            presenter listensTo get<AddSymbolToThemeNotifier>()

            ThemeListController(
                projectId.toString(),
                applicationScope.get(),
                get(),
                presenter,
                get(),
                get()
            )
        }

        scoped<DeleteThemeDialogScope> {
            provide<DeleteThemeDialogViewListener> {
                val presenter = DeleteThemeDialogPresenter(
                    themeId, themeName, projectScope.get<DeleteThemeDialogModel>()
                )

                presenter listensTo projectScope.get<DeleteThemeNotifier>()

                DeleteThemeDialogController(
                    themeId,
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    projectScope.get()
                )
            }
        }

        scoped<ValueOppositionWebsScope> {
            provide<ValueOppositionWebsViewListener> {
                val presenter = ValueOppositionWebsPresenter(
                    themeId.toString(),
                    get<ValueOppositionWebsModel>()
                )


                ValueOppositionWebsController(
                    themeId.toString(),
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter
                )
            }
        }
    }
}