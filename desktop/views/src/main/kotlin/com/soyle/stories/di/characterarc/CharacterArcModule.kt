package com.soyle.stories.di.characterarc

import com.soyle.stories.character.buildNewCharacter.*
import com.soyle.stories.character.createArcSection.CreateArcSectionController
import com.soyle.stories.character.createArcSection.CreateArcSectionControllerImpl
import com.soyle.stories.character.createArcSection.CreatedCharacterArcSectionNotifier
import com.soyle.stories.character.createArcSection.CreatedCharacterArcSectionReceiver
import com.soyle.stories.character.deleteCharacterArc.DeleteCharacterArcNotifier
import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryController
import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryControllerImpl
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.character.usecases.createPerspectiveCharacter.CreatePerspectiveCharacter
import com.soyle.stories.character.usecases.createPerspectiveCharacter.CreatePerspectiveCharacterUseCase
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogController
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogPresenter
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogState
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogViewListener
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogController
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
import com.soyle.stories.characterarc.eventbus.*
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionController
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionControllerImpl
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionNotifier
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogController
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogViewListener
import com.soyle.stories.characterarc.planNewCharacterArc.*
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionController
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionControllerImpl
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionNotifier
import com.soyle.stories.characterarc.usecaseControllers.ChangeThematicSectionValueController
import com.soyle.stories.characterarc.usecaseControllers.PromoteMinorCharacterController
import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangeCharacterArcSectionValue
import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangeCharacterArcSectionValueUseCase
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeleteCharacterArc
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeleteCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection
import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionUseCase
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcsUseCase
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionUseCase
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.changeCharacterPerspectiveProperty.ChangeCharacterPerspectivePropertyValueOutput
import com.soyle.stories.theme.changeCharacterPerspectiveProperty.CharacterPerspectivePropertyChangedNotifier
import com.soyle.stories.theme.changeCharacterPerspectiveProperty.CharacterPerspectivePropertyChangedReceiver
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonOutput
import com.soyle.stories.theme.removeCharacterFromComparison.RemoveCharacterFromComparisonController
import com.soyle.stories.theme.removeCharacterFromComparison.RemoveCharacterFromComparisonControllerImpl
import com.soyle.stories.theme.removeCharacterFromComparison.RemoveCharacterFromComparisonOutput
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValueUseCase
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValueUseCase
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunction
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunctionUseCase
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValueUseCase
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharactersUseCase
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacterUseCase
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparisonUseCase
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacterUseCase
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparisonUseCase

object CharacterArcModule {

    private fun InScope<ProjectScope>.useCases() {
        provide<ListAllCharacterArcs> {
            ListAllCharacterArcsUseCase(get(), get())
        }
        provide<BuildNewCharacter> {
            BuildNewCharacterUseCase(get(), get())
        }
        provide<PlanNewCharacterArc> {
            PlanNewCharacterArcUseCase(get(), get(), get())
        }
        provide<CompareCharacters> {
            CompareCharactersUseCase(get())
        }
        provide<IncludeCharacterInComparison> {
            IncludeCharacterInComparisonUseCase(get(), get())
        }
        provide<PromoteMinorCharacter> {
            PromoteMinorCharacterUseCase(get(), get())
        }
        provide<DemoteMajorCharacter> {
            DemoteMajorCharacterUseCase(get())
        }
        provide<DeleteCharacterArc> {
            DeleteCharacterArcUseCase(get())
        }
        provide<ChangeThematicSectionValue> {
            ChangeThematicSectionValueUseCase(get())
        }
        provide<ChangeStoryFunction> {
            ChangeStoryFunctionUseCase(get())
        }
        provide<ChangeCharacterPropertyValue> {
            ChangeCharacterPropertyValueUseCase(get())
        }
        provide<ChangeCharacterPerspectivePropertyValue> {
            ChangeCharacterPerspectivePropertyValueUseCase(get())
        }
        provide<RemoveCharacterFromComparison> {
            RemoveCharacterFromComparisonUseCase(get(), get())
        }
        provide<RenameCharacterArc> {
            RenameCharacterArcUseCase(get(), get())
        }
        provide<LinkLocationToCharacterArcSection> {
            LinkLocationToCharacterArcSectionUseCase(get(), get())
        }
        provide<UnlinkLocationFromCharacterArcSection> {
            UnlinkLocationFromCharacterArcSectionUseCase(get())
        }
        provide<CreatePerspectiveCharacter> { CreatePerspectiveCharacterUseCase(get(), get()) }
        provide<ChangeCharacterArcSectionValue> { ChangeCharacterArcSectionValueUseCase(get(), get()) }
    }

    private fun InScope<ProjectScope>.events() {

        provide(CreatedCharacterReceiver::class) { CreatedCharacterNotifier() }
        provide(CreatedCharacterArcReceiver::class) { CreatedCharacterArcNotifier() }
        provide(CharacterPerspectivePropertyChangedReceiver::class) { CharacterPerspectivePropertyChangedNotifier() }

        provide(CreatedCharacterArcSectionReceiver::class) {
            CreatedCharacterArcSectionNotifier()
        }

        provide(BuildNewCharacter.OutputPort::class) { BuildNewCharacterOutput(get(), get(), get()) }
        provide(CreatePerspectiveCharacter.OutputPort::class) { CreatePerspectiveCharacterOutput(get(), get()) }
        provide(PlanNewCharacterArc.OutputPort::class) { PlanNewCharacterArcOutput(get(), get()) }
        provide(IncludeCharacterInComparison.OutputPort::class) { IncludeCharacterInComparisonOutput(get()) }
        provide(PromoteMinorCharacter.OutputPort::class) { PromoteMinorCharacterOutput(get()) }
        provide(DemoteMajorCharacter.OutputPort::class) { DeleteCharacterArcNotifier(applicationScope.get()) }
        provide(ChangeStoryFunction.OutputPort::class) { ChangeStoryFunctionNotifier(applicationScope.get()) }
        provide(ChangeThematicSectionValue.OutputPort::class) { ChangeThematicSectionValueNotifier(applicationScope.get()) }
        provide(ChangeCharacterPropertyValue.OutputPort::class) { ChangeCharacterPropertyValueNotifier(applicationScope.get()) }
        provide(ChangeCharacterPerspectivePropertyValue.OutputPort::class) {
            ChangeCharacterPerspectivePropertyValueOutput(
                get()
            )
        }
        provide(RemoveCharacterFromComparison.OutputPort::class) { RemoveCharacterFromComparisonOutput(get(), get()) }

        provide(RenameCharacterArc.OutputPort::class) { RenameCharacterArcNotifier(applicationScope.get()) }
        provide(LinkLocationToCharacterArcSection.OutputPort::class) { LinkLocationToCharacterArcSectionNotifier(applicationScope.get()) }
        provide(UnlinkLocationFromCharacterArcSection.OutputPort::class) { UnlinkLocationFromCharacterArcSectionNotifier(applicationScope.get()) }
    }

    private fun InScope<ProjectScope>.controllers() {
        provide<PlanNewCharacterArcController> {
            PlanNewCharacterArcControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }
        provide<BuildNewCharacterController> {
            BuildNewCharacterControllerImpl(
                projectId.toString(),
                applicationScope.get(),
                get(),
                get(),
                get(),
                get()
            )
        }
        provide { ChangeThematicSectionValueController(applicationScope.get(), get(), get()) }
        provide<LinkLocationToCharacterArcSectionController> {
            LinkLocationToCharacterArcSectionControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }
        provide<UnlinkLocationFromCharacterArcSectionController> {
            UnlinkLocationFromCharacterArcSectionControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }
        provide<RemoveCharacterFromComparisonController> {
            RemoveCharacterFromComparisonControllerImpl(applicationScope.get(), get(), get())
        }
        provide {
            PromoteMinorCharacterController(applicationScope.get(), get(), get())
        }
        provide<RemoveCharacterFromStoryController> {
            RemoveCharacterFromStoryControllerImpl(applicationScope.get(), get(), get())
        }
        provide<CreateArcSectionController> {
            CreateArcSectionControllerImpl(applicationScope.get(), get(), get())
        }
    }


    private fun InScope<ProjectScope>.viewListeners() {
        provide<CreateCharacterDialogViewListener> {
            CreateCharacterDialogController(get())
        }
        provide<PlanCharacterArcDialogViewListener> {
            PlanCharacterArcDialogController(get())
        }
        provide<CreateArcSectionDialogViewListener> {
            val presenter = CreateArcSectionDialogPresenter(
                get<CreateArcSectionDialogState>()
            )

            CreateArcSectionDialogController(
                applicationScope.get(),
                get(),
                presenter,
                get(),
                get()
            )
        }
    }

    private fun InScope<ProjectScope>.views() {

    }

    init {

        scoped<ProjectScope> {
            useCases()
            events()
            controllers()
            viewListeners()
            views()
        }

        CharacterListModule

    }
}