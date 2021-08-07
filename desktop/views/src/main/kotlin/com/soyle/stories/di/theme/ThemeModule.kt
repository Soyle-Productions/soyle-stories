package com.soyle.stories.di.theme

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterNotifier
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.character.renameCharacter.CharacterRenamedNotifier
import com.soyle.stories.usecase.character.listCharactersAvailableToIncludeInTheme.ListCharactersAvailableToIncludeInTheme
import com.soyle.stories.usecase.character.listCharactersAvailableToIncludeInTheme.ListCharactersAvailableToIncludeInThemeUseCase
import com.soyle.stories.characterarc.changeSectionValue.*
import com.soyle.stories.characterarc.eventbus.ChangeCharacterPropertyValueNotifier
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.*
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.deleteLocation.DeletedLocationNotifier
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.location.renameLocation.LocationRenamedNotifier
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebController
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebControllerImpl
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebNotifier
import com.soyle.stories.theme.addSymbolDialog.*
import com.soyle.stories.theme.addSymbolToTheme.*
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionControllerImpl
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionNotifier
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeController
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeControllerImpl
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeNotifier
import com.soyle.stories.theme.changeCharacterChange.*
import com.soyle.stories.theme.changeCharacterPerspectiveProperty.ChangeCharacterPerspectivePropertyController
import com.soyle.stories.theme.changeCharacterPerspectiveProperty.ChangeCharacterPerspectivePropertyControllerImpl
import com.soyle.stories.theme.changeCharacterPropertyValue.ChangeCharacterPropertyController
import com.soyle.stories.theme.changeCharacterPropertyValue.ChangeCharacterPropertyValueControllerImpl
import com.soyle.stories.theme.changeThemeDetails.changeCentralConflict.CentralConflictChangedNotifier
import com.soyle.stories.theme.changeThemeDetails.changeCentralConflict.CentralConflictChangedReceiver
import com.soyle.stories.theme.changeThemeDetails.changeCentralConflict.ChangeCentralConflictController
import com.soyle.stories.theme.changeThemeDetails.changeCentralConflict.ChangeCentralConflictControllerImpl
import com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion.ChangedCentralMoralQuestionNotifier
import com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion.ChangedCentralMoralQuestionReceiver
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenameThemeController
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenameThemeControllerImpl
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeNotifier
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeReceiver
import com.soyle.stories.theme.characterConflict.CharacterConflictModule
import com.soyle.stories.theme.characterValueComparison.*
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialogController
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialogModel
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialogPresenter
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialogViewListener
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialogController
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialogModel
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialogPresenter
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialogViewListener
import com.soyle.stories.theme.createTheme.*
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
import com.soyle.stories.theme.deleteTheme.DeleteThemeOutput
import com.soyle.stories.theme.deleteTheme.ThemeDeletedNotifier
import com.soyle.stories.theme.deleteThemeDialog.*
import com.soyle.stories.theme.deleteValueWebDialog.*
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeNotifier
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonController
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonControllerImpl
import com.soyle.stories.theme.removeCharacterAsOpponent.*
import com.soyle.stories.theme.removeCharacterFromComparison.RemovedCharacterFromThemeNotifier
import com.soyle.stories.theme.removeCharacterFromComparison.RemovedCharacterFromThemeReceiver
import com.soyle.stories.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWebController
import com.soyle.stories.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWebControllerImpl
import com.soyle.stories.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWebNotifier
import com.soyle.stories.theme.removeSymbolFromTheme.RemoveSymbolFromThemeController
import com.soyle.stories.theme.removeSymbolFromTheme.RemoveSymbolFromThemeControllerImpl
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeNotifier
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
import com.soyle.stories.theme.renameSymbol.RenameSymbolOutput
import com.soyle.stories.theme.renameSymbol.RenamedSymbolNotifier
import com.soyle.stories.theme.renameSymbolicItems.RenameSymbolicItemController
import com.soyle.stories.theme.renameSymbolicItems.RenameSymbolicItemNotifier
import com.soyle.stories.theme.renameValueWeb.RenameValueWebController
import com.soyle.stories.theme.renameValueWeb.RenameValueWebControllerImpl
import com.soyle.stories.theme.renameValueWeb.RenameValueWebNotifier
import com.soyle.stories.theme.themeList.ThemeListController
import com.soyle.stories.theme.themeList.ThemeListModel
import com.soyle.stories.theme.themeList.ThemeListPresenter
import com.soyle.stories.theme.themeList.ThemeListViewListener
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsModel
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import com.soyle.stories.theme.useCharacterAsMainOpponent.*
import com.soyle.stories.theme.useCharacterAsOpponent.*
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWebUseCase
import com.soyle.stories.usecase.theme.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.usecase.theme.addSymbolToTheme.AddSymbolToThemeUseCase
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionUseCase
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToThemeUseCase
import com.soyle.stories.usecase.theme.changeCharacterChange.ChangeCharacterChange
import com.soyle.stories.usecase.theme.changeCharacterChange.ChangeCharacterChangeUseCase
import com.soyle.stories.usecase.theme.changeThemeDetails.ChangeCentralConflict
import com.soyle.stories.usecase.theme.changeThemeDetails.ChangeCentralMoralQuestion
import com.soyle.stories.usecase.theme.changeThemeDetails.ChangeThemeDetailsUseCase
import com.soyle.stories.usecase.theme.changeThemeDetails.RenameTheme
import com.soyle.stories.usecase.theme.compareCharacterValues.CompareCharacterValues
import com.soyle.stories.usecase.theme.compareCharacterValues.CompareCharacterValuesUseCase
import com.soyle.stories.usecase.theme.createTheme.CreateTheme
import com.soyle.stories.usecase.theme.createTheme.CreateThemeUseCase
import com.soyle.stories.usecase.theme.deleteTheme.DeleteTheme
import com.soyle.stories.usecase.theme.deleteTheme.DeleteThemeUseCase
import com.soyle.stories.usecase.theme.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.usecase.theme.examineCentralConflictOfTheme.ExamineCentralConflictOfThemeUseCase
import com.soyle.stories.usecase.theme.listAvailableEntitiesToAddToOpposition.ListAvailableEntitiesToAddToOpposition
import com.soyle.stories.usecase.theme.listAvailableEntitiesToAddToOpposition.ListAvailableEntitiesToAddToOppositionUseCase
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInThemeUseCase
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharactersUseCase
import com.soyle.stories.usecase.theme.listOppositionsInValueWeb.ListOppositionsInValueWeb
import com.soyle.stories.usecase.theme.listOppositionsInValueWeb.ListOppositionsInValueWebUseCase
import com.soyle.stories.usecase.theme.listSymbolsByTheme.ListSymbolsByTheme
import com.soyle.stories.usecase.theme.listSymbolsByTheme.ListSymbolsByThemeUseCase
import com.soyle.stories.usecase.theme.listSymbolsInTheme.ListSymbolsInTheme
import com.soyle.stories.usecase.theme.listSymbolsInTheme.ListSymbolsInThemeUseCase
import com.soyle.stories.usecase.theme.listThemes.ListThemes
import com.soyle.stories.usecase.theme.listThemes.ListThemesUseCase
import com.soyle.stories.usecase.theme.listValueWebsInTheme.ListValueWebsInTheme
import com.soyle.stories.usecase.theme.listValueWebsInTheme.ListValueWebsInThemeUseCase
import com.soyle.stories.usecase.theme.removeCharacterAsOpponent.RemoveCharacterAsOpponent
import com.soyle.stories.usecase.theme.removeCharacterAsOpponent.RemoveCharacterAsOpponentUseCase
import com.soyle.stories.usecase.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWeb
import com.soyle.stories.usecase.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWebUseCase
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemoveSymbolicItem
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemoveSymbolicItemUseCase
import com.soyle.stories.usecase.theme.removeValueWebFromTheme.RemoveValueWebFromTheme
import com.soyle.stories.usecase.theme.removeValueWebFromTheme.RemoveValueWebFromThemeUseCase
import com.soyle.stories.usecase.theme.renameOppositionValue.RenameOppositionValue
import com.soyle.stories.usecase.theme.renameOppositionValue.RenameOppositionValueUseCase
import com.soyle.stories.usecase.theme.renameSymbol.RenameSymbol
import com.soyle.stories.usecase.theme.renameSymbol.RenameSymbolUseCase
import com.soyle.stories.usecase.theme.renameSymbolicItems.RenameSymbolicItem
import com.soyle.stories.usecase.theme.renameSymbolicItems.RenameSymbolicItemUseCase
import com.soyle.stories.usecase.theme.renameValueWeb.RenameValueWeb
import com.soyle.stories.usecase.theme.renameValueWeb.RenameValueWebUseCase
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.ListAvailableCharactersToUseAsOpponents
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.UseCharacterAsMainOpponent
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.UseCharacterAsOpponent
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.UseCharacterAsOpponentUseCase
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsController
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsPresenter
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewListener

object ThemeModule {

    fun provideCreateTheme(scope: ProjectScope): CreateTheme = CreateThemeUseCase(scope.get())

    init {

        scoped<ProjectScope> {
            usecases()
            notifiers()
            controllers()
            gui()
        }

    }

    private fun InScope<ProjectScope>.usecases() {
        provide { provideCreateTheme(this) }
        provide<ListSymbolsByTheme> { ListSymbolsByThemeUseCase(get()) }
        provide<DeleteTheme> { DeleteThemeUseCase(get(), get()) }
        provide(
            ChangeCentralMoralQuestion::class,
            RenameTheme::class,
            ChangeCentralConflict::class
        ) {
            ChangeThemeDetailsUseCase(get())
        }
        provide<AddSymbolToTheme> { AddSymbolToThemeUseCase(get()) }
        provide<ListThemes> { ListThemesUseCase(get()) }
        provide<ListValueWebsInTheme> { ListValueWebsInThemeUseCase(get()) }
        provide<AddValueWebToTheme> { AddValueWebToThemeUseCase(get(), get()) }
        provide<RenameSymbol> { RenameSymbolUseCase(get(), get(), get()) }
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
        provide<ListAvailableEntitiesToAddToOpposition> {
            ListAvailableEntitiesToAddToOppositionUseCase(
                get(),
                get(),
                get()
            )
        }
        provide<CompareCharacterValues> { CompareCharacterValuesUseCase(get()) }
        provide<ListCharactersAvailableToIncludeInTheme> {
            ListCharactersAvailableToIncludeInThemeUseCase(
                get(),
                get()
            )
        }
        provide<ExamineCentralConflictOfTheme> { ExamineCentralConflictOfThemeUseCase(get(), get()) }
        provide<ListAvailablePerspectiveCharacters> { ListAvailablePerspectiveCharactersUseCase(get()) }
        provide(
            UseCharacterAsOpponent::class,
            UseCharacterAsMainOpponent::class,
            ListAvailableCharactersToUseAsOpponents::class
        ) { UseCharacterAsOpponentUseCase(get(), get()) }
        provide(
            ChangeCentralConflict::class,
            RenameTheme::class,
            ChangeCentralMoralQuestion::class
        ) {
            ChangeThemeDetailsUseCase(get())
        }
        provide<ChangeCharacterDesire> { ChangeCharacterDesireUseCase(get(), get()) }
        provide<ChangeCharacterPsychologicalWeakness> { ChangeCharacterPsychologicalWeaknessUseCase(get(), get()) }
        provide<ChangeCharacterMoralWeakness> { ChangeCharacterMoralWeaknessUseCase(get(), get()) }
        provide<ChangeCharacterChange> { ChangeCharacterChangeUseCase(get()) }
        provide<RemoveCharacterAsOpponent> { RemoveCharacterAsOpponentUseCase(get()) }
    }

    private fun InScope<ProjectScope>.notifiers() {
        provide(SymbolAddedToThemeReceiver::class) { SymbolAddedToThemeNotifier() }
        provide(CreatedThemeReceiver::class) { CreatedThemeNotifier() }
        provide(CharacterIncludedInThemeReceiver::class) { CharacterIncludedInThemeNotifier() }
        provide(CharacterUsedAsOpponentReceiver::class) { CharacterUsedAsOpponentNotifier() }
        provide(CharacterUsedAsMainOpponentReceiver::class) { CharacterUsedAsMainOpponentNotifier() }
        provide(RenamedThemeReceiver::class) { RenamedThemeNotifier() }
        provide(CentralConflictChangedReceiver::class) { CentralConflictChangedNotifier() }
        provide(ChangedCharacterArcSectionValueReceiver::class) { ChangedCharacterArcSectionValueNotifier() }
        provide(ChangedCharacterChangeReceiver::class) { ChangedCharacterChangeNotifier() }
        provide(CharacterRemovedAsOpponentReceiver::class) { CharacterRemovedAsOpponentNotifier() }
        provide(RemovedCharacterFromThemeReceiver::class) { RemovedCharacterFromThemeNotifier() }
        provide(ChangedCentralMoralQuestionReceiver::class) { ChangedCentralMoralQuestionNotifier() }


        provide(CreateTheme.OutputPort::class) {
            CreateThemeOutput(get(), get())
        }
        provide(DeleteTheme.OutputPort::class) {
            DeleteThemeOutput(get(), get())
        }
        provide(AddSymbolToTheme.OutputPort::class) {
            AddSymbolToThemeOutput(get())
        }
        provide(AddValueWebToTheme.OutputPort::class) {
            AddValueWebToThemeNotifier(get())
        }
        provide(RenameSymbol.OutputPort::class) {
            RenameSymbolOutput(get(), get(), get())
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
        provide(UseCharacterAsOpponent.OutputPort::class) {
            UseCharacterAsOpponentOutput(get(), get())
        }
        provide(UseCharacterAsMainOpponent.OutputPort::class) {
            UseCharacterAsMainOpponentOutput(get(), get())
        }
        provide(
            ChangeCharacterDesire.OutputPort::class,
            ChangeCharacterPsychologicalWeakness.OutputPort::class,
            ChangeCharacterMoralWeakness.OutputPort::class,
            ChangeCharacterArcSectionValue.OutputPort::class
        ) {
            ChangeCharacterArcSectionValueOutput(get(), get())
        }
        provide(ChangeCharacterChange.OutputPort::class) {
            ChangeCharacterChangeOutput(get())
        }
        provide(RemoveCharacterAsOpponent.OutputPort::class) {
            RemoveCharacterAsOpponentOutput(get())
        }
    }

    private fun InScope<ProjectScope>.controllers() {
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
        provide<UseCharacterAsOpponentController> {
            UseCharacterAsOpponentControllerImpl(applicationScope.get(), get(), get())
        }
        provide<UseCharacterAsMainOpponentController> {
            UseCharacterAsMainOpponentControllerImpl(applicationScope.get(), get(), get())
        }
        provide<ChangeCentralConflictController> {
            ChangeCentralConflictControllerImpl(applicationScope.get(), get(), get())
        }
        provide<ChangeSectionValueController> {
            ChangeSectionValueControllerImpl(applicationScope.get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
        }
        provide<ChangeCharacterChangeController> {
            ChangeCharacterChangeControllerImpl(applicationScope.get(), get(), get())
        }
        provide<ChangeCharacterPerspectivePropertyController> {
            ChangeCharacterPerspectivePropertyControllerImpl(applicationScope.get(), get(), get())
        }
        provide(RemoveCharacterAsOpponentController::class) {
            RemoveCharacterAsOpponentControllerImpl(applicationScope.get(), get(), get())
        }
    }

    private fun InScope<ProjectScope>.gui() {
        provide<CreateThemeDialogViewListener> {
            val presenter = CreateThemeDialogPresenter(
                get<CreateThemeDialogModel>()
            )

            presenter listensTo get<CreatedThemeNotifier>()

            CreateThemeDialogController(
                presenter,
                get()
            )
        }

        provide<CreateSymbolDialogViewListener> {
            val presenter = CreateSymbolDialogPresenter(
                get<CreateSymbolDialogModel>()
            )

            presenter listensTo get<SymbolAddedToThemeNotifier>()
            presenter listensTo get<CreatedThemeNotifier>()
            presenter listensTo get<ThemeDeletedNotifier>()
            presenter listensTo get<RenamedThemeNotifier>()

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

            presenter listensTo get<CreatedThemeNotifier>()
            presenter listensTo get<ThemeDeletedNotifier>()
            presenter listensTo get<RenamedThemeNotifier>()
            presenter listensTo get<SymbolAddedToThemeNotifier>()
            presenter listensTo get<SymbolRemovedFromThemeNotifier>()
            presenter listensTo get<RenamedSymbolNotifier>()

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

                presenter listensTo projectScope.get<ThemeDeletedNotifier>()

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

                presenter listensTo projectScope.get<SymbolRemovedFromThemeNotifier>()

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
                presenter listensTo projectScope.get<CharacterIncludedInThemeNotifier>()
                presenter listensTo projectScope.get<RemovedCharacterFromThemeNotifier>()
                presenter listensTo projectScope.get<ChangeCharacterPropertyValueNotifier>()
                presenter listensTo projectScope.get<RenameOppositionValueNotifier>()
                presenter listensTo projectScope.get<RenameValueWebNotifier>()

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

                presenter listensTo projectScope.get<CreatedCharacterNotifier>()
                presenter listensTo projectScope.get<CharacterRenamedNotifier>()
                presenter listensTo projectScope.get<RemovedCharacterNotifier>()

                presenter listensTo projectScope.get<CreateNewLocationNotifier>()
                presenter listensTo projectScope.get<LocationRenamedNotifier>()
                presenter listensTo projectScope.get<DeletedLocationNotifier>()

                presenter listensTo projectScope.get<SymbolAddedToThemeNotifier>()
                presenter listensTo projectScope.get<RenamedSymbolNotifier>()
                presenter listensTo projectScope.get<SymbolRemovedFromThemeNotifier>()

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

        CharacterConflictModule
    }
}