package com.soyle.stories.di.theme

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterNotifier
import com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme.ListCharactersAvailableToIncludeInTheme
import com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme.ListCharactersAvailableToIncludeInThemeUseCase
import com.soyle.stories.characterarc.eventbus.ChangeCharacterPropertyValueNotifier
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonNotifier
import com.soyle.stories.characterarc.eventbus.RemoveCharacterFromLocalStoryNotifier
import com.soyle.stories.characterarc.eventbus.RenameCharacterNotifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.location.events.DeleteLocationNotifier
import com.soyle.stories.location.events.RenameLocationNotifier
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebController
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebControllerImpl
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebNotifier
import com.soyle.stories.theme.addSymbolDialog.*
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeController
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeControllerImpl
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeNotifier
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionControllerImpl
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionNotifier
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeController
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeControllerImpl
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeNotifier
import com.soyle.stories.theme.changeCharacterPropertyValue.ChangeCharacterPropertyController
import com.soyle.stories.theme.changeCharacterPropertyValue.ChangeCharacterPropertyValueControllerImpl
import com.soyle.stories.theme.characterConflict.*
import com.soyle.stories.theme.characterValueComparison.*
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialogController
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialogModel
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialogPresenter
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialogViewListener
import com.soyle.stories.theme.createSymbolDialog.*
import com.soyle.stories.theme.createTheme.CreateThemeController
import com.soyle.stories.theme.createTheme.CreateThemeControllerImpl
import com.soyle.stories.theme.createTheme.CreateThemeNotifier
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogController
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogModel
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogPresenter
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogViewListener
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialogController
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialogModel
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialogPresenter
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialogViewListener
import com.soyle.stories.theme.deleteSymbolDialog.*
import com.soyle.stories.theme.deleteTheme.DeleteThemeController
import com.soyle.stories.theme.deleteTheme.DeleteThemeControllerImpl
import com.soyle.stories.theme.deleteTheme.DeleteThemeNotifier
import com.soyle.stories.theme.deleteThemeDialog.*
import com.soyle.stories.theme.deleteValueWebDialog.*
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonController
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonControllerImpl
import com.soyle.stories.theme.removeCharacterFromComparison.RemoveCharacterFromComparisonNotifier
import com.soyle.stories.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWebController
import com.soyle.stories.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWebControllerImpl
import com.soyle.stories.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWebNotifier
import com.soyle.stories.theme.removeSymbolFromTheme.RemoveSymbolFromThemeController
import com.soyle.stories.theme.removeSymbolFromTheme.RemoveSymbolFromThemeControllerImpl
import com.soyle.stories.theme.removeSymbolFromTheme.RemoveSymbolFromThemeNotifier
import com.soyle.stories.theme.removeSymbolicItem.RemoveSymbolicItemController
import com.soyle.stories.theme.removeSymbolicItem.RemoveSymbolicItemControllerImpl
import com.soyle.stories.theme.removeSymbolicItem.RemoveSymbolicItemNotifier
import com.soyle.stories.theme.removeValueWebFromTheme.RemoveValueWebFromThemeController
import com.soyle.stories.theme.removeValueWebFromTheme.RemoveValueWebFromThemeControllerImpl
import com.soyle.stories.theme.removeValueWebFromTheme.RemoveValueWebFromThemeNotifier
import com.soyle.stories.theme.renameOppositionValue.RenameOppositionValueController
import com.soyle.stories.theme.renameOppositionValue.RenameOppositionValueControllerImpl
import com.soyle.stories.theme.renameOppositionValue.RenameOppositionValueNotifier
import com.soyle.stories.theme.renameSymbol.RenameSymbolController
import com.soyle.stories.theme.renameSymbol.RenameSymbolControllerImpl
import com.soyle.stories.theme.renameSymbol.RenameSymbolNotifier
import com.soyle.stories.theme.renameSymbolicItems.RenameSymbolicItemController
import com.soyle.stories.theme.renameSymbolicItems.RenameSymbolicItemNotifier
import com.soyle.stories.theme.renameTheme.RenameThemeController
import com.soyle.stories.theme.renameTheme.RenameThemeControllerImpl
import com.soyle.stories.theme.renameTheme.RenameThemeNotifier
import com.soyle.stories.theme.renameValueWeb.RenameValueWebController
import com.soyle.stories.theme.renameValueWeb.RenameValueWebControllerImpl
import com.soyle.stories.theme.renameValueWeb.RenameValueWebNotifier
import com.soyle.stories.theme.themeList.ThemeListController
import com.soyle.stories.theme.themeList.ThemeListModel
import com.soyle.stories.theme.themeList.ThemeListPresenter
import com.soyle.stories.theme.themeList.ThemeListViewListener
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsModel
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWebUseCase
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToThemeUseCase
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOppositionUseCase
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToThemeUseCase
import com.soyle.stories.theme.usecases.compareCharacterValues.CompareCharacterValues
import com.soyle.stories.theme.usecases.compareCharacterValues.CompareCharacterValuesUseCase
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreateThemeUseCase
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeleteThemeUseCase
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfThemeUseCase
import com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition.ListAvailableEntitiesToAddToOpposition
import com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition.ListAvailableEntitiesToAddToOppositionUseCase
import com.soyle.stories.theme.usecases.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import com.soyle.stories.theme.usecases.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInThemeUseCase
import com.soyle.stories.theme.usecases.listOppositionsInValueWeb.ListOppositionsInValueWeb
import com.soyle.stories.theme.usecases.listOppositionsInValueWeb.ListOppositionsInValueWebUseCase
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByThemeUseCase
import com.soyle.stories.theme.usecases.listSymbolsInTheme.ListSymbolsInTheme
import com.soyle.stories.theme.usecases.listSymbolsInTheme.ListSymbolsInThemeUseCase
import com.soyle.stories.theme.usecases.listThemes.ListThemes
import com.soyle.stories.theme.usecases.listThemes.ListThemesUseCase
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ListValueWebsInTheme
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ListValueWebsInThemeUseCase
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.RemoveOppositionFromValueWeb
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.RemoveOppositionFromValueWebUseCase
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromThemeUseCase
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemoveSymbolicItem
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemoveSymbolicItemUseCase
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.RemoveValueWebFromTheme
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.RemoveValueWebFromThemeUseCase
import com.soyle.stories.theme.usecases.renameOppositionValue.RenameOppositionValue
import com.soyle.stories.theme.usecases.renameOppositionValue.RenameOppositionValueUseCase
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbolUseCase
import com.soyle.stories.theme.usecases.renameSymbolicItems.RenameSymbolicItem
import com.soyle.stories.theme.usecases.renameSymbolicItems.RenameSymbolicItemUseCase
import com.soyle.stories.theme.usecases.renameTheme.RenameTheme
import com.soyle.stories.theme.usecases.renameTheme.RenameThemeUseCase
import com.soyle.stories.theme.usecases.renameValueWeb.RenameValueWeb
import com.soyle.stories.theme.usecases.renameValueWeb.RenameValueWebUseCase
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
        provide<DeleteTheme> { DeleteThemeUseCase(get(), get()) }
        provide<RenameTheme> { RenameThemeUseCase(get()) }
        provide<AddSymbolToTheme> { AddSymbolToThemeUseCase(get()) }
        provide<ListThemes> { ListThemesUseCase(get()) }
        provide<ListValueWebsInTheme> { ListValueWebsInThemeUseCase(get()) }
        provide<AddValueWebToTheme> { AddValueWebToThemeUseCase(get(), get()) }
        provide<RemoveSymbolFromTheme> { RemoveSymbolFromThemeUseCase(get()) }
        provide<RenameSymbol> { RenameSymbolUseCase(get()) }
        provide<ListOppositionsInValueWeb> { ListOppositionsInValueWebUseCase(get()) }
        provide<AddOppositionToValueWeb> { AddOppositionToValueWebUseCase(get(), get()) }
        provide<RenameOppositionValue> { RenameOppositionValueUseCase(get()) }
        provide<RemoveValueWebFromTheme> { RemoveValueWebFromThemeUseCase(get()) }
        provide<RenameValueWeb> { RenameValueWebUseCase(get()) }
        provide<RemoveOppositionFromValueWeb> { RemoveOppositionFromValueWebUseCase(get()) }
        provide<ListSymbolsInTheme> { ListSymbolsInThemeUseCase(get()) }
        provide<AddSymbolicItemToOpposition> { AddSymbolicItemToOppositionUseCase(get(), get(), get()) }
        provide<RenameSymbolicItem> { RenameSymbolicItemUseCase(get()) }
        provide<RemoveSymbolicItem> { RemoveSymbolicItemUseCase(get()) }
        provide<ListAvailableEntitiesToAddToOpposition> { ListAvailableEntitiesToAddToOppositionUseCase(get(), get(), get()) }
        provide<CompareCharacterValues> { CompareCharacterValuesUseCase(get()) }
        provide<ListCharactersAvailableToIncludeInTheme> { ListCharactersAvailableToIncludeInThemeUseCase(get(), get()) }
        provide<ListAvailableOppositionValuesForCharacterInTheme> { ListAvailableOppositionValuesForCharacterInThemeUseCase(get()) }
        provide<ExamineCentralConflictOfTheme> { ExamineCentralConflictOfThemeUseCase(get()) }
    }

    private fun InScope<ProjectScope>.notifiers()
    {
        provide(CreateTheme.OutputPort::class) {
            CreateThemeNotifier(get())
        }
        provide(DeleteTheme.OutputPort::class) {
            DeleteThemeNotifier(get())
        }
        provide(RenameTheme.OutputPort::class) {
            RenameThemeNotifier()
        }
        provide(AddSymbolToTheme.OutputPort::class) {
            AddSymbolToThemeNotifier()
        }
        provide(AddValueWebToTheme.OutputPort::class) {
            AddValueWebToThemeNotifier(get())
        }
        provide(RemoveSymbolFromTheme.OutputPort::class) {
            RemoveSymbolFromThemeNotifier().also {
                get<RemoveSymbolicItemControllerImpl>() listensTo it
            }
        }
        provide(RenameSymbol.OutputPort::class) {
            RenameSymbolNotifier().also {
                get<RenameSymbolicItemController>() listensTo it
            }
        }
        provide(AddOppositionToValueWeb.OutputPort::class) {
            AddOppositionToValueWebNotifier(get())
        }
        provide(RenameOppositionValue.OutputPort::class) {
            RenameOppositionValueNotifier()
        }
        provide(RemoveValueWebFromTheme.OutputPort::class) {
            RemoveValueWebFromThemeNotifier()
        }
        provide(RenameValueWeb.OutputPort::class) {
            RenameValueWebNotifier()
        }
        provide(RemoveOppositionFromValueWeb.OutputPort::class) {
            RemoveOppositionFromValueWebNotifier()
        }
        provide(AddSymbolicItemToOpposition.OutputPort::class) {
            AddSymbolicItemToOppositionNotifier(get(), get())
        }
        provide(RenameSymbolicItem.OutputPort::class) {
            RenameSymbolicItemNotifier()
        }
        provide(RemoveSymbolicItem.OutputPort::class) {
            RemoveSymbolicItemNotifier()
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
        provide<AddValueWebToThemeController> {
            AddValueWebToThemeControllerImpl(applicationScope.get(), get(), get())
        }
        provide<RemoveSymbolFromThemeController> {
            RemoveSymbolFromThemeControllerImpl(applicationScope.get(), get(), get())
        }
        provide<RenameSymbolController> {
            RenameSymbolControllerImpl(applicationScope.get(), get(), get())
        }
        provide<AddOppositionToValueWebController> {
            AddOppositionToValueWebControllerImpl(applicationScope.get(), get(), get())
        }
        provide<RenameOppositionValueController> {
            RenameOppositionValueControllerImpl(applicationScope.get(), get(), get())
        }
        provide<RemoveValueWebFromThemeController> {
            RemoveValueWebFromThemeControllerImpl(applicationScope.get(), get(), get())
        }
        provide<RenameValueWebController> {
            RenameValueWebControllerImpl(applicationScope.get(), get(), get())
        }
        provide<RemoveOppositionFromValueWebController> {
            RemoveOppositionFromValueWebControllerImpl(applicationScope.get(), get(), get())
        }
        provide<AddSymbolicItemToOppositionController> {
            AddSymbolicItemToOppositionControllerImpl(applicationScope.get(), get(), get())
        }
        provide { RenameSymbolicItemController(applicationScope.get(), get(), get()) }
        provide(RemoveSymbolicItemController::class) {
            RemoveSymbolicItemControllerImpl(applicationScope.get(), get(), get())
        }
        provide<IncludeCharacterInComparisonController> {
            IncludeCharacterInComparisonControllerImpl(applicationScope.get(), get(), get())
        }
        provide<ChangeCharacterPropertyController> {
            ChangeCharacterPropertyValueControllerImpl(applicationScope.get(), get(), get())
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
            presenter listensTo get<RemoveSymbolFromThemeNotifier>()
            presenter listensTo get<RenameSymbolNotifier>()

            ThemeListController(
                projectId.toString(),
                applicationScope.get(),
                get(),
                presenter,
                get(),
                get(),
                get()
            )
        }

        provide<CreateOppositionValueDialogViewListener> {
            val presenter = CreateOppositionValueDialogPresenter(
                get<CreateOppositionValueDialogModel>()
            )

            presenter listensTo get<AddOppositionToValueWebNotifier>()

            CreateOppositionValueDialogController(
                presenter,
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

        scoped<DeleteSymbolDialogScope> {
            provide<DeleteSymbolDialogViewListener> {
                val presenter = DeleteSymbolDialogPresenter(
                    symbolId, symbolName, projectScope.get<DeleteSymbolDialogModel>()
                )

                presenter listensTo projectScope.get<RemoveSymbolFromThemeNotifier>()

                DeleteSymbolDialogController(
                    symbolId,
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    projectScope.get()
                )
            }
        }

        scoped<DeleteValueWebDialogScope> {
            provide<DeleteValueWebDialogViewListener> {
                val presenter = DeleteValueWebDialogPresenter(
                    valueWebId, valueWebName, projectScope.get<DeleteValueWebDialogModel>()
                )

                presenter listensTo projectScope.get<RemoveValueWebFromThemeNotifier>()

                DeleteValueWebDialogController(
                    valueWebId,
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

                presenter listensTo projectScope.get<AddValueWebToThemeNotifier>()
                presenter listensTo projectScope.get<RenameValueWebNotifier>()
                presenter listensTo projectScope.get<RemoveValueWebFromThemeNotifier>()

                presenter listensTo projectScope.get<AddOppositionToValueWebNotifier>()
                presenter listensTo projectScope.get<RenameOppositionValueNotifier>()
                presenter listensTo projectScope.get<RemoveOppositionFromValueWebNotifier>()

                presenter listensTo projectScope.get<AddSymbolicItemToOppositionNotifier>()
                presenter listensTo projectScope.get<RenameSymbolicItemNotifier>()
                presenter listensTo projectScope.get<RemoveSymbolicItemNotifier>()

                ValueOppositionWebsController(
                    themeId.toString(),
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    presenter
                )
            }
        }

        scoped<CharacterValueComparisonScope> {
            provide<CharacterValueComparisonViewListener> {
                val presenter = CharacterValueComparisonPresenter(
                    type.themeId.toString(),
                    get<CharacterValueComparisonModel>()
                )

                presenter listensTo projectScope.get<AddSymbolicItemToOppositionNotifier>()
                presenter listensTo projectScope.get<RemoveSymbolicItemNotifier>()
                presenter listensTo projectScope.get<IncludeCharacterInComparisonNotifier>()
                presenter listensTo projectScope.get<RemoveCharacterFromComparisonNotifier>()
                presenter listensTo projectScope.get<ChangeCharacterPropertyValueNotifier>()

                CharacterValueComparisonController(
                    type.themeId.toString(),
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get()
                )
            }
        }

        provide<CreateValueWebDialogViewListener> {
            val presenter = CreateValueWebDialogPresenter(
                get<CreateValueWebDialogModel>()
            )

            presenter listensTo get<AddValueWebToThemeNotifier>()

            CreateValueWebDialogController(
                presenter,
                get()
            )
        }

        scoped<AddSymbolDialogScope> {
            provide<AddSymbolDialogViewListener> {
                val presenter = AddSymbolDialogPresenter(
                    themeId,
                    oppositionId,
                    get<AddSymbolDialogModel>()
                )

                presenter listensTo projectScope.get<BuildNewCharacterNotifier>()
                presenter listensTo projectScope.get<RenameCharacterNotifier>()
                presenter listensTo projectScope.get<RemoveCharacterFromLocalStoryNotifier>()

                presenter listensTo projectScope.get<CreateNewLocationNotifier>()
                presenter listensTo projectScope.get<RenameLocationNotifier>()
                presenter listensTo projectScope.get<DeleteLocationNotifier>()

                presenter listensTo projectScope.get<AddSymbolToThemeNotifier>()
                presenter listensTo projectScope.get<RenameSymbolNotifier>()
                presenter listensTo projectScope.get<RemoveSymbolFromThemeNotifier>()

                presenter listensTo projectScope.get<AddSymbolicItemToOppositionNotifier>()

                AddSymbolDialogController(
                    oppositionId,
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter,
                    projectScope.get()
                )
            }
        }

        scoped<CharacterConflictScope> {
            provide<CharacterConflictViewListener> {

                val presenter = CharacterConflictPresenter(
                    themeId,
                    get<CharacterConflictModel>()
                )

                CharacterConflictController(
                    themeId,
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter
                )
            }
        }
    }
}