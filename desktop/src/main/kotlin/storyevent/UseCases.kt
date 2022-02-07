package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.character.add.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.character.add.AddCharacterToStoryEventOutput
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventController
import com.soyle.stories.storyevent.character.remove.CharacterRemovedFromStoryEventNotifier
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventOutput
import com.soyle.stories.storyevent.coverage.*
import com.soyle.stories.storyevent.coverage.cover.CoverStoryEventController
import com.soyle.stories.storyevent.coverage.cover.CoverStoryEventInSceneOutput
import com.soyle.stories.storyevent.coverage.cover.StoryEventCoverageController
import com.soyle.stories.storyevent.coverage.uncover.UncoverStoryEventController
import com.soyle.stories.storyevent.coverage.uncover.UncoverStoryEventFromSceneOutput
import com.soyle.stories.storyevent.create.CreateStoryEventOutput
import com.soyle.stories.storyevent.details.StoryEventDetailsController
import com.soyle.stories.storyevent.list.ListStoryEventsController
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.storyevent.remove.RemoveStoryEventOutput
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.storyevent.rename.RenameStoryEventOutput
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeOutput
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventOutput
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.character.involve.GetAvailableCharactersToInvolveInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.GetAvailableCharactersToInvolveInStoryEventUseCase
import com.soyle.stories.usecase.storyevent.character.involve.InvolveCharacterInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.InvolveCharacterInStoryEventUseCase
import com.soyle.stories.usecase.storyevent.character.remove.GetPotentialChangesOfRemovingCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.character.remove.GetPotentialChangesOfRemovingCharacterFromStoryEventUseCase
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEventUseCase
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInScene
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInSceneUseCase
import com.soyle.stories.usecase.storyevent.coverage.uncover.GetPotentialChangesFromUncoveringStoryEvent
import com.soyle.stories.usecase.storyevent.coverage.uncover.GetPotentialChangesFromUncoveringStoryEventUseCase
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromSceneUseCase
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetails
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetailsUseCase
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEventsUseCase
import com.soyle.stories.usecase.storyevent.remove.GetPotentialChangesOfRemovingStoryEventFromProject
import com.soyle.stories.usecase.storyevent.remove.GetPotentialChangesOfRemovingStoryEventFromProjectUseCase
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProjectUseCase
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEvent
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEventUseCase
import com.soyle.stories.usecase.storyevent.time.adjust.AdjustStoryEventsTime
import com.soyle.stories.usecase.storyevent.time.adjust.AdjustStoryEventsTimeUseCase
import com.soyle.stories.usecase.storyevent.time.reschedule.RescheduleStoryEvent
import com.soyle.stories.usecase.storyevent.time.reschedule.RescheduleStoryEventUseCase

object UseCases {

    init {
        scoped<ProjectScope> {
            provide { StoryEventTimeService(get<StoryEventRepository>()) }

            createStoryEvent()
            getStoryEventDetails()
            listStoryEvents()
            renameStoryEvent()
            rescheduleStoryEvent()
            storyEventCoverage()
            coverStoryEventInScene()
            uncoverStoryEventInScene()
            involveCharacterInStoryEvent()
            removeCharacterFromStoryEvent()
            adjustStoryEventsTime()
            removeStoryEvent()
        }
    }

    private fun InProjectScope.createStoryEvent() {
        provide<CreateStoryEvent> { CreateStoryEventUseCase(get(), get()) }
        provide<CreateStoryEvent.OutputPort> { CreateStoryEventOutput(get(), get()) }
    }

    private fun InProjectScope.getStoryEventDetails() {
        provide<GetStoryEventDetails> { GetStoryEventDetailsUseCase(get()) }
        provide<StoryEventDetailsController> {
            StoryEventDetailsController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get()
            )
        }
    }

    private fun InProjectScope.listStoryEvents() {
        provide<ListAllStoryEvents> { ListAllStoryEventsUseCase(get()) }
        provide<ListStoryEventsController> { ListStoryEventsController(applicationScope.get(), get()) }
    }

    private fun InProjectScope.renameStoryEvent() {
        provide<RenameStoryEvent> { RenameStoryEventUseCase(get()) }
        provide<RenameStoryEvent.OutputPort> { RenameStoryEventOutput(get()) }
        provide<RenameStoryEventController> { RenameStoryEventController(applicationScope.get(), get(), get(), get()) }
    }

    private fun InProjectScope.rescheduleStoryEvent() {
        provide<RescheduleStoryEvent> { RescheduleStoryEventUseCase(get()) }
        provide<RescheduleStoryEvent.OutputPort> { RescheduleStoryEventOutput(get()) }
        provide<RescheduleStoryEventController> {
            RescheduleStoryEventController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

    private fun InProjectScope.adjustStoryEventsTime() {
        provide<AdjustStoryEventsTime> { AdjustStoryEventsTimeUseCase(get()) }
        provide<AdjustStoryEventsTime.OutputPort> { AdjustStoryEventsTimeOutput(get()) }
        provide<AdjustStoryEventsTimeController> {
            AdjustStoryEventsTimeController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

    private fun InProjectScope.storyEventCoverage() {
        provide<StoryEventCoverageController> {
            StoryEventCoverageController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

    private fun InProjectScope.coverStoryEventInScene() {
        provide<CoverStoryEventController> {
            CoverStoryEventController.Implementation(
                Project.Id(this.projectId),
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get()
            )
        }
        provide<CoverStoryEventInScene> { CoverStoryEventInSceneUseCase(get(), get()) }
        provide<CoverStoryEventInScene.OutputPort> { CoverStoryEventInSceneOutput(get()) }
    }

    private fun InProjectScope.involveCharacterInStoryEvent() {
        provide<GetAvailableCharactersToInvolveInStoryEvent> {
            GetAvailableCharactersToInvolveInStoryEventUseCase(get(), get())
        }
        provide<InvolveCharacterInStoryEvent> {
            InvolveCharacterInStoryEventUseCase(get(), get())
        }
        provide<AddCharacterToStoryEventController> {
            AddCharacterToStoryEventController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get(),
                get()
            )
        }
        provide<InvolveCharacterInStoryEvent.OutputPort> {
            AddCharacterToStoryEventOutput(get(), get(), get(), get(), get())
        }
    }

    private fun InProjectScope.removeCharacterFromStoryEvent() {
        provide<RemoveCharacterFromStoryEvent> {
            RemoveCharacterFromStoryEventUseCase(get(), get())
        }
        provide(RemoveCharacterFromStoryEvent.OutputPort::class) {
            RemoveCharacterFromStoryEventOutput(get<CharacterRemovedFromStoryEventNotifier>())
        }
        provide<GetPotentialChangesOfRemovingCharacterFromStoryEvent> {
            GetPotentialChangesOfRemovingCharacterFromStoryEventUseCase(get(), get(), get())
        }
        provide<RemoveCharacterFromStoryEventController> {
            RemoveCharacterFromStoryEventController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

    private fun InProjectScope.uncoverStoryEventInScene() {
        provide<GetPotentialChangesFromUncoveringStoryEvent> {
            GetPotentialChangesFromUncoveringStoryEventUseCase(get(), get(), get())
        }
        provide<UncoverStoryEventController> {
            UncoverStoryEventController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
        provide<UncoverStoryEventFromScene> { UncoverStoryEventFromSceneUseCase(get()) }
        provide<UncoverStoryEventFromScene.OutputPort> { UncoverStoryEventFromSceneOutput(get()) }
    }

    private fun InProjectScope.removeStoryEvent() {
        provide<GetPotentialChangesOfRemovingStoryEventFromProject> {
            GetPotentialChangesOfRemovingStoryEventFromProjectUseCase(get(), get(), get())
        }
        provide<RemoveStoryEventFromProject> { RemoveStoryEventFromProjectUseCase(get()) }
        provide<RemoveStoryEventFromProject.OutputPort> { RemoveStoryEventOutput(get()) }
        provide<RemoveStoryEventController> {
            RemoveStoryEventController.Implementation(
                applicationScope.get<ThreadTransformer>().asyncScope,
                applicationScope.get<ThreadTransformer>().guiContext,
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

}