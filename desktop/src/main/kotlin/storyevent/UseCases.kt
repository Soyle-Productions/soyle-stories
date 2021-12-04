package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.coverage.*
import com.soyle.stories.storyevent.create.CreateStoryEventOutput
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
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInScene
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInSceneUseCase
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromSceneUseCase
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEventsUseCase
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
            listStoryEvents()
            renameStoryEvent()
            rescheduleStoryEvent()
            storyEventCoverage()
            coverStoryEventInScene()
            uncoverStoryEventInScene()
            adjustStoryEventsTime()
            removeStoryEvent()
        }
    }

    private fun InProjectScope.createStoryEvent() {
        provide<CreateStoryEvent> { CreateStoryEventUseCase(get()) }
        provide<CreateStoryEvent.OutputPort> { CreateStoryEventOutput(get(), get()) }
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
        provide<CoverStoryEventInScene.OutputPort> { CoverStoryEventInSceneOutput(get(), get(), get(), get()) }
    }

    private fun InProjectScope.uncoverStoryEventInScene() {
        provide<UncoverStoryEventController> {
            UncoverStoryEventController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get(),
                get(),
                getUncoverStoryEventPrompt = { _, _ -> TODO() }
            )
        }
        provide<UncoverStoryEventFromScene> { UncoverStoryEventFromSceneUseCase(get(), get()) }
        provide<UncoverStoryEventFromScene.OutputPort> { UncoverStoryEventFromSceneOutput(get(), get()) }
    }

    private fun InProjectScope.removeStoryEvent() {
        provide<RemoveStoryEventFromProject> { RemoveStoryEventFromProjectUseCase(get(), get()) }
        provide<RemoveStoryEventFromProject.OutputPort> { RemoveStoryEventOutput(get(), get()) }
        provide<RemoveStoryEventController> {
            RemoveStoryEventController(
                applicationScope.get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

}