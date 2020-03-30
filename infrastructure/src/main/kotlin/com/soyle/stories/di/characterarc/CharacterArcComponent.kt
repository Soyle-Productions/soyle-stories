/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 4:07 PM
 */
package com.soyle.stories.di.characterarc

import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStoryUseCase
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStoryUseCase
import com.soyle.stories.characterarc.characterList.CharacterListController
import com.soyle.stories.characterarc.characterList.CharacterListModel
import com.soyle.stories.characterarc.characterList.CharacterListPresenter
import com.soyle.stories.characterarc.characterList.CharacterListViewListener
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogController
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
import com.soyle.stories.characterarc.eventbus.*
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogController
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogViewListener
import com.soyle.stories.characterarc.usecaseControllers.ChangeThematicSectionValueController
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeleteCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcsUseCase
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArcUseCase
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructureUseCase
import com.soyle.stories.common.ThreadTransformerImpl
import com.soyle.stories.di.modules.DataComponent
import com.soyle.stories.di.project.LayoutComponent
import com.soyle.stories.entities.Project
import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.project.ProjectScope
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
import tornadofx.Component
import tornadofx.FX
import tornadofx.ScopedInstance

class CharacterArcComponent : Component(), ScopedInstance {

    override val scope: ProjectScope = super.scope as ProjectScope
    private val dataComponent: DataComponent by inject(overrideScope = FX.defaultScope)
    private val layoutComponent: LayoutComponent by inject()

    val listAllCharacterArcs: ListAllCharacterArcs by lazy {
        ListAllCharacterArcsUseCase(
            scope.projectId,
            dataComponent.characterRepository,
            dataComponent.characterArcRepository
        )
    }
    val buildNewCharacter: BuildNewCharacter by lazy {
        BuildNewCharacterUseCase(Project.Id(scope.projectId), dataComponent.characterRepository)
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
            scope.projectId,
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
            scope.projectId,
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
            scope.projectId,
            RemoveCharacterFromComparisonUseCase(dataComponent.context),
            dataComponent.context
        )
    }

    val eventBus: EventBus by lazy {
        object : EventBus {
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
        }
    }

    private val characterListPresenter by lazy {
        CharacterListPresenter(
            ThreadTransformerImpl,
            find<CharacterListModel>(),
            eventBus
        )
    }

    val listAllCharacterArcsOutputPort: ListAllCharacterArcs.OutputPort
        get() = characterListPresenter

    val buildNewCharacterOutputPort: BuildNewCharacter.OutputPort
        get() = eventBus.buildNewCharacter as BuildNewCharacter.OutputPort
    val planNewCharacterArcOutputPort: PlanNewCharacterArc.OutputPort
        get() = eventBus.planNewCharacterArc as PlanNewCharacterArc.OutputPort
    val includeCharacterInComparisonOutputPort: IncludeCharacterInComparison.OutputPort
        get() = eventBus.includeCharacterInComparison as IncludeCharacterInComparison.OutputPort
    val promoteMinorCharacterOutputPort: PromoteMinorCharacter.OutputPort
        get() = eventBus.promoteMinorCharacter as PromoteMinorCharacter.OutputPort
    val deleteLocalCharacterArcOutputPort: DeleteLocalCharacterArc.OutputPort
        get() = eventBus.deleteLocalCharacterArc as DeleteLocalCharacterArc.OutputPort
    val removeCharacterFromStoryOutputPort: RemoveCharacterFromLocalStory.OutputPort
        get() = eventBus.removeCharacterFromStory as RemoveCharacterFromLocalStory.OutputPort
    val changeStoryFunctionOutputPort: ChangeStoryFunction.OutputPort
        get() = eventBus.changeStoryFunction as ChangeStoryFunction.OutputPort
    val changeThematicSectionValueOutputPort: ChangeThematicSectionValue.OutputPort
        get() = eventBus.changeThematicSectionValue as ChangeThematicSectionValue.OutputPort
    val changeCentralMoralQuestionOutputPort: ChangeCentralMoralQuestion.OutputPort
        get() = eventBus.changeCentralMoralQuestion as ChangeCentralMoralQuestion.OutputPort
    val changeCharacterPropertyValueOutputPort: ChangeCharacterPropertyValue.OutputPort
        get() = eventBus.changeCharacterPropertyValue as ChangeCharacterPropertyValue.OutputPort
    val changeCharacterPerspectivePropertyValueOutputPort: ChangeCharacterPerspectivePropertyValue.OutputPort
        get() = eventBus.changeCharacterPerspectivePropertyValue as ChangeCharacterPerspectivePropertyValue.OutputPort
    val removeCharacterFromLocalComparisonOutputPort: RemoveCharacterFromLocalComparison.OutputPort
        get() = eventBus.removeCharacterFromLocalComparison as RemoveCharacterFromLocalComparison.OutputPort


    val changeThematicSectionValueController = ChangeThematicSectionValueController(
        ThreadTransformerImpl, changeThematicSectionValue, changeThematicSectionValueOutputPort
    )

    val characterListViewListener: CharacterListViewListener by lazy {
        CharacterListController(
            ThreadTransformerImpl,
            listAllCharacterArcs,
            listAllCharacterArcsOutputPort,
            layoutComponent.openTool,
            layoutComponent.openToolOutputPort,
            removeCharacterFromLocalStory,
            removeCharacterFromStoryOutputPort,
            deleteLocalCharacterArc,
            deleteLocalCharacterArcOutputPort
        )
    }

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

}