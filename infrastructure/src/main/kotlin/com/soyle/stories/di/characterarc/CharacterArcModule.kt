package com.soyle.stories.di.characterarc

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterControllerImpl
import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterNotifier
import com.soyle.stories.character.renameCharacter.RenameCharacterController
import com.soyle.stories.character.renameCharacter.RenameCharacterControllerImpl
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStoryUseCase
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacterUseCase
import com.soyle.stories.characterarc.changeCentralMoralQuestion.ChangeCentralMoralQuestionController
import com.soyle.stories.characterarc.changeCentralMoralQuestion.ChangeCentralMoralQuestionNotifier
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonScope
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogController
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
import com.soyle.stories.characterarc.eventbus.*
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionController
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionControllerImpl
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionNotifier
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogController
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogViewListener
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionController
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionControllerImpl
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionNotifier
import com.soyle.stories.characterarc.usecaseControllers.*
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
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructureUseCase
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.Project
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventController
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventControllerImpl
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestionUseCase
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
			ListAllCharacterArcsUseCase(projectId, get(), get())
		}
		provide<BuildNewCharacter> {
			BuildNewCharacterUseCase(Project.Id(projectId), get())
		}
		provide<PlanNewCharacterArc> {
			PlanNewCharacterArcUseCase(get(), get(), get(), get())
		}
		provide<ViewBaseStoryStructure> {
			ViewBaseStoryStructureUseCase(get(), get())
		}
		provide<CompareCharacters> {
			CompareCharactersUseCase(get())
		}
		provide<IncludeCharacterInComparison> {
			IncludeCharacterInComparisonUseCase(get(), get(), get())
		}
		provide<PromoteMinorCharacter> {
			PromoteMinorCharacterUseCase(get(), get(), get())
		}
		provide<DemoteMajorCharacter> {
			DemoteMajorCharacterUseCase(get())
		}
		provide<RemoveCharacterFromStory> {
			RemoveCharacterFromStoryUseCase(get(), get(), get())
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
		provide<ChangeCentralMoralQuestion> {
			ChangeCentralMoralQuestionUseCase(get())
		}
		provide<ChangeCharacterPropertyValue> {
			ChangeCharacterPropertyValueUseCase(get())
		}
		provide<ChangeCharacterPerspectivePropertyValue> {
			ChangeCharacterPerspectivePropertyValueUseCase(get())
		}
		provide<RemoveCharacterFromComparison> {
			RemoveCharacterFromComparisonUseCase(get())
		}
		provide<RenameCharacter> {
			RenameCharacterUseCase(get(), get())
		}
		provide<RenameCharacterArc> {
			RenameCharacterArcUseCase(get(), get(), get())
		}
		provide<LinkLocationToCharacterArcSection> {
			LinkLocationToCharacterArcSectionUseCase(get(), get())
		}
		provide<UnlinkLocationFromCharacterArcSection> {
			UnlinkLocationFromCharacterArcSectionUseCase(get())
		}
	}

	private fun InScope<ProjectScope>.events() {
		provide<CharacterArcEvents> {
			object : CharacterArcEvents {
				override val buildNewCharacter: Notifier<BuildNewCharacter.OutputPort> =
				  BuildNewCharacterNotifier()
				override val planNewCharacterArc: Notifier<PlanNewCharacterArc.OutputPort> =
				  PlanNewCharacterArcNotifier()
				override val includeCharacterInComparison: Notifier<IncludeCharacterInComparison.OutputPort> =
				  IncludeCharacterInComparisonNotifier()
				override val promoteMinorCharacter: Notifier<PromoteMinorCharacter.OutputPort> =
				  PromoteMinorCharacterNotifier()
				override val deleteLocalCharacterArc: Notifier<DemoteMajorCharacter.OutputPort> =
				  DeleteLocalCharacterArcNotifier()
				override val removeCharacterFromStory: Notifier<RemoveCharacterFromStory.OutputPort> =
				  RemoveCharacterFromLocalStoryNotifier().also {
					  get<RemoveCharacterFromStoryEventControllerImpl>() listensTo it
				  }
				override val changeStoryFunction: Notifier<ChangeStoryFunction.OutputPort> =
				  ChangeStoryFunctionNotifier()
				override val changeThematicSectionValue =
				  ChangeThematicSectionValueNotifier()
				override val changeCentralMoralQuestion: Notifier<ChangeCentralMoralQuestion.OutputPort> =
				  ChangeCentralMoralQuestionNotifier()
				override val changeCharacterPropertyValue: Notifier<ChangeCharacterPropertyValue.OutputPort> =
				  ChangeCharacterPropertyValueNotifier()
				override val changeCharacterPerspectivePropertyValue: Notifier<ChangeCharacterPerspectivePropertyValue.OutputPort> =
				  ChangeCharacterPerspectivePropertyValueNotifier()
				override val removeCharacterFromLocalComparison: Notifier<RemoveCharacterFromComparison.OutputPort> =
				  RemoveCharacterFromLocalComparisonNotifier()
				override val renameCharacter: Notifier<RenameCharacter.OutputPort> =
				  RenameCharacterNotifier()
				override val renameCharacterArc: Notifier<RenameCharacterArc.OutputPort> =
				  RenameCharacterArcNotifier()
				override val linkLocationToCharacterArcSection: Notifier<LinkLocationToCharacterArcSection.OutputPort> =
				  LinkLocationToCharacterArcSectionNotifier()
				override val unlinkLocationFromCharacterArcSection: Notifier<UnlinkLocationFromCharacterArcSection.OutputPort> =
				  UnlinkLocationFromCharacterArcSectionNotifier()
			}
		}

		provide(BuildNewCharacter.OutputPort::class) { get<CharacterArcEvents>().buildNewCharacter as BuildNewCharacterNotifier }
		provide(PlanNewCharacterArc.OutputPort::class) { get<CharacterArcEvents>().planNewCharacterArc as PlanNewCharacterArcNotifier }
		provide(IncludeCharacterInComparison.OutputPort::class) { get<CharacterArcEvents>().includeCharacterInComparison as IncludeCharacterInComparisonNotifier }
		provide(PromoteMinorCharacter.OutputPort::class) { get<CharacterArcEvents>().promoteMinorCharacter as PromoteMinorCharacterNotifier }
		provide(DemoteMajorCharacter.OutputPort::class) { get<CharacterArcEvents>().deleteLocalCharacterArc as DeleteLocalCharacterArcNotifier }
		provide(RemoveCharacterFromStory.OutputPort::class) { get<CharacterArcEvents>().removeCharacterFromStory as RemoveCharacterFromLocalStoryNotifier }
		provide(ChangeStoryFunction.OutputPort::class) { get<CharacterArcEvents>().changeStoryFunction as ChangeStoryFunctionNotifier }
		provide(ChangeThematicSectionValue.OutputPort::class) { get<CharacterArcEvents>().changeThematicSectionValue as ChangeThematicSectionValueNotifier }
		provide(ChangeCentralMoralQuestion.OutputPort::class) { get<CharacterArcEvents>().changeCentralMoralQuestion as ChangeCentralMoralQuestionNotifier }
		provide(ChangeCharacterPropertyValue.OutputPort::class) { get<CharacterArcEvents>().changeCharacterPropertyValue as ChangeCharacterPropertyValueNotifier }
		provide(ChangeCharacterPerspectivePropertyValue.OutputPort::class) { get<CharacterArcEvents>().changeCharacterPerspectivePropertyValue as ChangeCharacterPerspectivePropertyValueNotifier }
		provide(RemoveCharacterFromComparison.OutputPort::class) { get<CharacterArcEvents>().removeCharacterFromLocalComparison as RemoveCharacterFromLocalComparisonNotifier }
		provide(RenameCharacter.OutputPort::class) { get<CharacterArcEvents>().renameCharacter as RenameCharacterNotifier }
		provide(RenameCharacterArc.OutputPort::class) { get<CharacterArcEvents>().renameCharacterArc as RenameCharacterArcNotifier }
		provide(LinkLocationToCharacterArcSection.OutputPort::class) { get<CharacterArcEvents>().linkLocationToCharacterArcSection as LinkLocationToCharacterArcSectionNotifier }
		provide(UnlinkLocationFromCharacterArcSection.OutputPort::class) { get<CharacterArcEvents>().unlinkLocationFromCharacterArcSection as UnlinkLocationFromCharacterArcSectionNotifier }
	}

	private fun InScope<ProjectScope>.controllers() {

		provide<BuildNewCharacterController> { BuildNewCharacterControllerImpl(applicationScope.get(), get(), get()) }
		provide { ChangeThematicSectionValueController(applicationScope.get(), get(), get()) }
		provide<LinkLocationToCharacterArcSectionController> { LinkLocationToCharacterArcSectionControllerImpl(applicationScope.get(), get(), get()) }
		provide<UnlinkLocationFromCharacterArcSectionController> { UnlinkLocationFromCharacterArcSectionControllerImpl(applicationScope.get(), get(), get()) }
		provide<RenameCharacterController> { RenameCharacterControllerImpl(applicationScope.get(), get(), get()) }
	}



	private fun InScope<ProjectScope>.viewListeners() {
		provide<CreateCharacterDialogViewListener> {
			CreateCharacterDialogController(get(), get())
		}
		provide<PlanCharacterArcDialogViewListener> {
			PlanCharacterArcDialogController(get(), get())
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

		scoped<CharacterComparisonScope> {
			provide {
				IncludeCharacterInComparisonController(themeId, projectScope.get(), projectScope.get())
			}
			provide {
				PromoteMinorCharacterController(themeId, projectScope.get(), projectScope.get())
			}
			provide {
				DeleteLocalCharacterArcController(themeId, projectScope.get(), projectScope.get())
			}
			provide {
				ChangeStoryFunctionController(themeId, projectScope.get(), projectScope.get())
			}
			provide {
				ChangeCentralMoralQuestionController(themeId, projectScope.get(), projectScope.get())
			}
			provide {
				ChangeCharacterPropertyController(themeId, projectScope.get(), projectScope.get())
			}
			provide {
				ChangeCharacterPerspectivePropertyController(themeId, projectScope.get(), projectScope.get())
			}
			provide {
				RemoveCharacterFromLocalComparisonController(projectScope.get(), projectScope.get())
			}
		}

		BaseStoryStructureModule
		CharacterComparisonModule
		CharacterListModule

	}
}