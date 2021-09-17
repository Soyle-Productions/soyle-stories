package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.create.*
import com.soyle.stories.storyevent.list.*
import com.soyle.stories.storyevent.remove.*
import com.soyle.stories.storyevent.rename.RenameStoryEventPrompt
import com.soyle.stories.storyevent.rename.RenameStoryEventPromptPresenter
import com.soyle.stories.storyevent.rename.RenameStoryEventPromptView
import com.soyle.stories.storyevent.rename.StoryEventRenamedNotifier
import com.soyle.stories.storyevent.time.*
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimePrompt
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventPrompt
import com.soyle.stories.storyevent.timeline.TimelinePresenter
import com.soyle.stories.storyevent.timeline.TimelineToolPresenter
import com.soyle.stories.storyevent.timeline.TimelineView
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.stage.Modality
import javafx.stage.StageStyle
import kotlinx.coroutines.Job
import tornadofx.UIComponent
import tornadofx.onChange
import tornadofx.onChangeOnce

object Presentation {

    init {
        scoped<ProjectScope> {
            provide<StoryEventListTool> {
                object : StoryEventListTool {
                    override fun invoke(projectId: Project.Id): Node {
                        val presenter = StoryEventListPresenter(
                            projectId = projectId,
                            createStoryEventController = get(),
                            renameStoryEventController = get(),
                            rescheduleStoryEventController = get(),
                            adjustStoryEventsTimeController = get(),
                            removeStoryEventController = get(),
                            listStoryEventsController = get(),
                            requestToViewStoryEventInTimeline = get<TimelineToolPresenter>()::viewTimeline,
                            storyEventCreated = get<StoryEventCreatedNotifier>(),
                            storyEventRenamed = get<StoryEventRenamedNotifier>(),
                            storyEventRescheduled = get<StoryEventRescheduledNotifier>(),
                            storyEventNoLongerHappens = get<StoryEventNoLongerHappensNotifier>(),
                            threadTransformer = applicationScope.get()
                        )

                        val view = StoryEventListToolView(
                            presenter,
                            presenter.viewModel,
                            get()
                        )

                        if (projectId.uuid == this@provide.projectId) {
                            DI.getRegisteredTypes(this@provide)[StoryEventListToolView::class] = view
                        }

                        return view
                    }
                }
            }

            provide<StoryEventListCell> {
                object : StoryEventListCell {
                    override fun invoke(): ListCell<StoryEventListItemViewModel> {
                        return object : ListCell<StoryEventListItemViewModel>() {
                            override fun updateItem(item: StoryEventListItemViewModel?, empty: Boolean) {
                                super.updateItem(item, empty)
                                if (empty || item == null) {
                                    graphic = null
                                    text = null
                                } else {
                                    graphic = StoryEventListItemView(item)
                                }
                            }
                        }
                    }
                }
            }

            provide<CreateStoryEventPrompt> {
                object : CreateStoryEventPrompt {
                    override fun promptToCreateStoryEvent(relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative?) {
                        val presenter = CreateStoryEventPromptPresenter(
                            relativeTo,
                            get<CreateStoryEventController>(),
                            applicationScope.get()
                        )
                        CreateStoryEventPromptView(presenter, presenter.viewModel).apply {
                            val stage = openModal(modality = Modality.APPLICATION_MODAL, owner = get<WorkBench>().currentStage)
                            presenter.viewModel.isCompleted.onChangeUntil({ it == true }) {
                                if (it == true) stage?.hide()
                            }
                        }
                    }
                }
            }

            provide<RenameStoryEventPrompt> {
                object : RenameStoryEventPrompt {
                    override fun promptForNewName(storyEventId: StoryEvent.Id, currentName: String) {
                        val presenter = RenameStoryEventPromptPresenter(
                            storyEventId,
                            currentName,
                            get(),
                            applicationScope.get()
                        )
                        RenameStoryEventPromptView(presenter.viewModel, presenter).apply {
                            val stage = openModal(modality = Modality.APPLICATION_MODAL, owner = get<WorkBench>().currentStage)
                            presenter.viewModel.isCompleted.onChangeUntil({ it == true }) {
                                if (it == true) stage?.hide()
                            }
                        }
                    }
                }
            }

            provide(RescheduleStoryEventPrompt::class, AdjustStoryEventsTimePrompt::class) {
                object : RescheduleStoryEventPrompt, AdjustStoryEventsTimePrompt {

                    override fun promptForNewTime(storyEventId: StoryEvent.Id, currentTime: Long) {
                        val presenter = TimeAdjustmentPromptPresenter(
                            storyEventId,
                            currentTime,
                            get(),
                            get(),
                            applicationScope.get()
                        )

                        val stage = TimeAdjustmentPromptView(presenter, presenter.viewModel).openModal(owner = get<WorkBench>().root.scene?.window)!!
                        presenter.viewModel.isCompleted.onChangeUntil({ it == true }) {
                            if (it == true) stage.hide()
                        }
                    }

                    override fun promptForAdjustmentAmount(storyEventIds: Set<StoryEvent.Id>) {
                        val presenter = TimeAdjustmentPromptPresenter(
                            storyEventIds,
                            get(),
                            get(),
                            applicationScope.get()
                        )

                        val stage = TimeAdjustmentPromptView(presenter, presenter.viewModel).openModal(owner = get<WorkBench>().root.scene?.window)!!
                        presenter.viewModel.isCompleted.onChangeUntil({ it == true }) {
                            if (it == true) stage.hide()
                        }
                    }
                }
            }

            provide<RemoveStoryEventConfirmation> {
                object : RemoveStoryEventConfirmation {
                    override fun requestDeleteStoryEventConfirmation(storyEventIds: Set<StoryEvent.Id>) {
                        val viewModel = RemoveStoryEventConfirmationPromptViewModel(applicationScope.get())
                        val presenter = RemoveStoryEventConfirmationPromptPresenter(
                            storyEventIds,
                            viewModel,
                            get(),
                            get()
                        )
                        val stageInit = lazy {
                            RemoveStoryEventConfirmationPromptView(presenter, viewModel).openModal(owner = get<WorkBench>().root.scene?.window)
                        }
                        viewModel.showing().onChangeWithCurrent {
                            if (it == true) stageInit.value?.show()
                            else if (stageInit.isInitialized()) stageInit.value?.hide()
                        }
                    }
                }
            }

            timeline()
        }
    }

    private fun InProjectScope.timeline() {
        provide {
            TimelineToolPresenter(this, applicationScope.get(), get(), get())
        }
        provide { TimelinePresenter() }
        provide { TimelineView() }
    }

}