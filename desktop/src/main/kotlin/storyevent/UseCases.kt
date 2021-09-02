package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
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
            createStoryEvent()
            listStoryEvents()
            renameStoryEvent()
            rescheduleStoryEvent()
            adjustStoryEventsTime()
            removeStoryEvent()
        }
    }

    private fun InProjectScope.createStoryEvent() {
        provide<CreateStoryEvent> { CreateStoryEventUseCase(get()) }
        provide<CreateStoryEvent.OutputPort> { CreateStoryEventOutput(get()) }
    }

    private fun InProjectScope.listStoryEvents() {
        provide<ListAllStoryEvents> { ListAllStoryEventsUseCase(get()) }
        provide<ListStoryEventsController> { ListStoryEventsController(applicationScope.get(), get()) }
    }

    private fun InProjectScope.renameStoryEvent() {
        provide<RenameStoryEvent> { RenameStoryEventUseCase(get()) }
        provide<RenameStoryEvent.OutputPort> { RenameStoryEventOutput(get()) }
        provide<RenameStoryEventController> { RenameStoryEventController(applicationScope.get(), get(), get()) }
    }

    private fun InProjectScope.rescheduleStoryEvent() {
        provide<RescheduleStoryEvent> { RescheduleStoryEventUseCase(get()) }
        provide<RescheduleStoryEvent.OutputPort> { RescheduleStoryEventOutput(get()) }
        provide<RescheduleStoryEventController> { RescheduleStoryEventController(applicationScope.get(), get(), get()) }
    }

    private fun InProjectScope.adjustStoryEventsTime() {
        provide<AdjustStoryEventsTime> { AdjustStoryEventsTimeUseCase(get()) }
        provide<AdjustStoryEventsTime.OutputPort> { AdjustStoryEventsTimeOutput(get()) }
        provide<AdjustStoryEventsTimeController> { AdjustStoryEventsTimeController(applicationScope.get(), get(), get()) }
    }

    private fun InProjectScope.removeStoryEvent() {
        provide<RemoveStoryEventFromProject> { RemoveStoryEventFromProjectUseCase(get()) }
        provide<RemoveStoryEventFromProject.OutputPort> { RemoveStoryEventOutput(get()) }
        provide<RemoveStoryEventController> { RemoveStoryEventController(applicationScope.get(), get(), get(), get()) }
    }

}