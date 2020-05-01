package com.soyle.stories.character

import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStoryUseCase
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStoryUseCase
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacterUseCase
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogController
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
import com.soyle.stories.characterarc.eventbus.*
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionNotifier
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogController
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogViewListener
import com.soyle.stories.characterarc.usecaseControllers.*
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeleteCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcsUseCase
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructureUseCase
import com.soyle.stories.entities.Project
import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.gui.SingleThreadTransformer
import com.soyle.stories.soylestories.DataComponent
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
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparisonUseCase
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparisonUseCase
import java.util.*

class CharacterArcComponent(
  projectId: UUID,
  dataComponent: DataComponent
) {

	val listAllCharacterArcs: ListAllCharacterArcs by lazy {
		ListAllCharacterArcsUseCase(
		  projectId,
		  dataComponent.characterRepository,
		  dataComponent.characterArcRepository
		)
	}
	val buildNewCharacter: BuildNewCharacter by lazy {
		BuildNewCharacterUseCase(Project.Id(projectId), dataComponent.characterRepository)
	}
	val planCharacterArc: PlanNewCharacterArc by lazy {
		PlanNewCharacterArcUseCase(
		  dataComponent.characterRepository,
		  dataComponent.themeRepository,
		  dataComponent.characterArcSectionRepository,
		  promoteMinorCharacter
		)
	}
	val viewBaseStoryStructure: ViewBaseStoryStructure by lazy {
		ViewBaseStoryStructureUseCase(dataComponent.themeRepository, dataComponent.characterArcSectionRepository)
	}
	val compareCharacters: CompareCharacters by lazy {
		CompareCharactersUseCase(dataComponent.context)
	}
	val includeCharacterInComparison: IncludeCharacterInComparison by lazy {
		IncludeCharacterInComparisonUseCase(
		  dataComponent.characterRepository,
		  dataComponent.themeRepository,
		  dataComponent.characterArcSectionRepository
		)
	}
	val promoteMinorCharacter: PromoteMinorCharacter by lazy {
		PromoteMinorCharacterUseCase(
		  dataComponent.themeRepository,
		  dataComponent.characterArcRepository,
		  dataComponent.characterArcSectionRepository
		)
	}
	val demoteMajorCharacter: DemoteMajorCharacter by lazy {
		DemoteMajorCharacterUseCase(dataComponent.context)
	}
	val removeCharacterFromLocalStory: RemoveCharacterFromLocalStory by lazy {
		RemoveCharacterFromLocalStoryUseCase(
		  projectId,
		  dataComponent.context,
		  RemoveCharacterFromStoryUseCase(
			dataComponent.characterRepository,
			dataComponent.themeRepository,
			dataComponent.characterArcSectionRepository
		  )
		)
	}
	val deleteLocalCharacterArc: DeleteLocalCharacterArc by lazy {
		DeleteLocalCharacterArcUseCase(
		  projectId,
		  DeleteCharacterArcUseCase(demoteMajorCharacter),
		  dataComponent.context
		)
	}
	val changeThematicSectionValue: ChangeThematicSectionValue by lazy {
		ChangeThematicSectionValueUseCase(dataComponent.characterArcSectionRepository)
	}
	val changeStoryFunction: ChangeStoryFunction by lazy {
		ChangeStoryFunctionUseCase(dataComponent.context)
	}
	val changeCentralMoralQuestion: ChangeCentralMoralQuestion by lazy {
		ChangeCentralMoralQuestionUseCase(dataComponent.context)
	}
	val changeCharacterPropertyValue: ChangeCharacterPropertyValue by lazy {
		ChangeCharacterPropertyValueUseCase(dataComponent.context)
	}
	val changeCharacterPerspectivePropertyValue: ChangeCharacterPerspectivePropertyValue by lazy {
		ChangeCharacterPerspectivePropertyValueUseCase(dataComponent.context)
	}
	val removeCharacterFromLocalComparison: RemoveCharacterFromLocalComparison by lazy {
		RemoveCharacterFromLocalComparisonUseCase(
		  projectId,
		  RemoveCharacterFromComparisonUseCase(dataComponent.context),
		  dataComponent.context
		)
	}
	val renameCharacter: RenameCharacter by lazy {
		RenameCharacterUseCase(
		  dataComponent.characterRepository, dataComponent.themeRepository
		)
	}
	val renameCharacterArc: RenameCharacterArc by lazy {
		RenameCharacterArcUseCase(
		  dataComponent.characterRepository, dataComponent.themeRepository, dataComponent.characterArcRepository
		)
	}

	val characterArcEvents: CharacterArcEvents by lazy {
		object : CharacterArcEvents {
			override val buildNewCharacter: Notifier<BuildNewCharacter.OutputPort> =
			  BuildNewCharacterNotifier()
			override val planNewCharacterArc: Notifier<PlanNewCharacterArc.OutputPort> =
			  PlanNewCharacterArcNotifier()
			override val includeCharacterInComparison: Notifier<IncludeCharacterInComparison.OutputPort> =
			  IncludeCharacterInComparisonNotifier()
			override val promoteMinorCharacter: Notifier<PromoteMinorCharacter.OutputPort> =
			  PromoteMinorCharacterNotifier()
			override val deleteLocalCharacterArc: Notifier<DeleteLocalCharacterArc.OutputPort> =
			  DeleteLocalCharacterArcNotifier()
			override val removeCharacterFromStory: Notifier<RemoveCharacterFromLocalStory.OutputPort> =
			  RemoveCharacterFromLocalStoryNotifier()
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
			override val removeCharacterFromLocalComparison: Notifier<RemoveCharacterFromLocalComparison.OutputPort> =
			  RemoveCharacterFromLocalComparisonNotifier()
			override val renameCharacter: Notifier<RenameCharacter.OutputPort> =
			  RenameCharacterNotifier()
			override val renameCharacterArc: Notifier<RenameCharacterArc.OutputPort> =
			  RenameCharacterArcNotifier()
			override val linkLocationToCharacterArcSection: Notifier<LinkLocationToCharacterArcSection.OutputPort> =
			  LinkLocationToCharacterArcSectionNotifier()
		}
	}

	val buildNewCharacterOutputPort: BuildNewCharacter.OutputPort
		get() = characterArcEvents.buildNewCharacter as BuildNewCharacter.OutputPort
	val planNewCharacterArcOutputPort: PlanNewCharacterArc.OutputPort
		get() = characterArcEvents.planNewCharacterArc as PlanNewCharacterArc.OutputPort
	val includeCharacterInComparisonOutputPort: IncludeCharacterInComparison.OutputPort
		get() = characterArcEvents.includeCharacterInComparison as IncludeCharacterInComparison.OutputPort
	val promoteMinorCharacterOutputPort: PromoteMinorCharacter.OutputPort
		get() = characterArcEvents.promoteMinorCharacter as PromoteMinorCharacter.OutputPort
	val deleteLocalCharacterArcOutputPort: DeleteLocalCharacterArc.OutputPort
		get() = characterArcEvents.deleteLocalCharacterArc as DeleteLocalCharacterArc.OutputPort
	val removeCharacterFromStoryOutputPort: RemoveCharacterFromLocalStory.OutputPort
		get() = characterArcEvents.removeCharacterFromStory as RemoveCharacterFromLocalStory.OutputPort
	val changeStoryFunctionOutputPort: ChangeStoryFunction.OutputPort
		get() = characterArcEvents.changeStoryFunction as ChangeStoryFunction.OutputPort
	val changeThematicSectionValueOutputPort: ChangeThematicSectionValue.OutputPort
		get() = characterArcEvents.changeThematicSectionValue as ChangeThematicSectionValue.OutputPort
	val changeCentralMoralQuestionOutputPort: ChangeCentralMoralQuestion.OutputPort
		get() = characterArcEvents.changeCentralMoralQuestion as ChangeCentralMoralQuestion.OutputPort
	val changeCharacterPropertyValueOutputPort: ChangeCharacterPropertyValue.OutputPort
		get() = characterArcEvents.changeCharacterPropertyValue as ChangeCharacterPropertyValue.OutputPort
	val changeCharacterPerspectivePropertyValueOutputPort: ChangeCharacterPerspectivePropertyValue.OutputPort
		get() = characterArcEvents.changeCharacterPerspectivePropertyValue as ChangeCharacterPerspectivePropertyValue.OutputPort
	val removeCharacterFromLocalComparisonOutputPort: RemoveCharacterFromLocalComparison.OutputPort
		get() = characterArcEvents.removeCharacterFromLocalComparison as RemoveCharacterFromLocalComparison.OutputPort
	val renameCharacterOutputPort: RenameCharacter.OutputPort
		get() = characterArcEvents.renameCharacter as RenameCharacter.OutputPort
	val renameCharacterArcOutputPort: RenameCharacterArc.OutputPort
		get() = characterArcEvents.renameCharacterArc as RenameCharacterArc.OutputPort

	val changeThematicSectionValueController = ChangeThematicSectionValueController(
	  SingleThreadTransformer, changeThematicSectionValue, changeThematicSectionValueOutputPort
	)

	val createCharacterDialogViewListener: CreateCharacterDialogViewListener by lazy {
		CreateCharacterDialogController(
		  buildNewCharacter,
		  buildNewCharacterOutputPort
		)
	}

	val planCharacterArcDialogViewListener: PlanCharacterArcDialogViewListener by lazy {
		PlanCharacterArcDialogController(
		  planCharacterArc,
		  planNewCharacterArcOutputPort
		)
	}

	fun includeCharacterInComparisonController(themeId: String) = IncludeCharacterInComparisonController(
	  themeId,
	  includeCharacterInComparison,
	  includeCharacterInComparisonOutputPort
	)

	fun promoteMinorCharacterController(themeId: String) = PromoteMinorCharacterController(
	  themeId,
	  promoteMinorCharacter,
	  promoteMinorCharacterOutputPort
	)

	fun deleteLocalCharacterArcController(themeId: String) = DeleteLocalCharacterArcController(
	  themeId,
	  deleteLocalCharacterArc,
	  deleteLocalCharacterArcOutputPort
	)

	fun changeStoryFunctionController(themeId: String) = ChangeStoryFunctionController(
	  themeId,
	  changeStoryFunction,
	  changeStoryFunctionOutputPort
	)

	fun changeCentralMoralQuestionController(themeId: String) = ChangeCentralMoralQuestionController(
	  themeId,
	  changeCentralMoralQuestion,
	  changeCentralMoralQuestionOutputPort
	)

	fun changeCharacterPropertyController(themeId: String) = ChangeCharacterPropertyController(
	  themeId,
	  changeCharacterPropertyValue,
	  changeCharacterPropertyValueOutputPort
	)

	fun changeCharacterPerspectivePropertyController(themeId: String) = ChangeCharacterPerspectivePropertyController(
	  themeId,
	  changeCharacterPerspectivePropertyValue,
	  changeCharacterPerspectivePropertyValueOutputPort
	)

	fun removeCharacterFromLocalComparisonController(themeId: String) = RemoveCharacterFromLocalComparisonController(
	  themeId,
	  removeCharacterFromLocalComparison,
	  removeCharacterFromLocalComparisonOutputPort
	)
}