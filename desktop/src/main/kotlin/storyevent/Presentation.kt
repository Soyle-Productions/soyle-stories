package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.Locale
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.character.add.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventController
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventPromptLocale
import com.soyle.stories.storyevent.character.remove.ramifications.RemoveCharacterFromStoryEventRamificationsReportLocale
import com.soyle.stories.storyevent.create.*
import com.soyle.stories.storyevent.details.StoryEventDetailsDependencies
import com.soyle.stories.storyevent.item.StoryEventItemMenuComponent
import com.soyle.stories.storyevent.item.StoryEventItemSelection
import com.soyle.stories.storyevent.list.*
import com.soyle.stories.storyevent.remove.*
import com.soyle.stories.storyevent.remove.ramifications.RemoveStoryEventFromStoryRamificationsReportLocale
import com.soyle.stories.storyevent.rename.*
import com.soyle.stories.storyevent.time.*
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimePrompt
import com.soyle.stories.storyevent.time.StoryEventTimeChangePromptPresenter
import com.soyle.stories.storyevent.time.normalization.NormalizationPromptPresenter
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventPrompt
import com.soyle.stories.storyevent.timeline.*
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderComponent
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderCreateButtonComponent
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderOptionsButtonComponent
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPort
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPortComponent
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewportContext
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGrid
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRulerComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabelComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenu
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetails
import javafx.beans.property.BooleanProperty
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.ListCell
import javafx.stage.Modality
import kotlinx.coroutines.CoroutineScope
import tornadofx.Scope
import kotlin.coroutines.CoroutineContext

object Presentation {

    init {
        scoped<ProjectScope> {
            provide<StoryEventListTool> {
                object : StoryEventListTool {
                    override fun invoke(projectId: Project.Id): Node {
                        if (DI.getRegisteredTypes(this@provide).hasInstanceOf(StoryEventListToolView::class)) {
                            return DI.getRegisteredTypes(this@provide)[StoryEventListToolView::class] as StoryEventListToolView
                        }

                        val presenter = StoryEventListPresenter(
                            this@provide,
                            projectId = projectId,
                            createStoryEventController = get(),
                            getStoryEventDetailsController = get(),
                            renameStoryEventController = get(),
                            rescheduleStoryEventController = get(),
                            adjustStoryEventsTimeController = get(),
                            removeStoryEventController = get(),
                            storyEventCoverageController = get(),
                            listStoryEventsController = get(),
                            requestToViewStoryEventInTimeline = get<TimelineToolPresenter>()::viewTimeline,
                            storyEventCreated = get<StoryEventCreatedNotifier>(),
                            storyEventRenamed = get<StoryEventRenamedNotifier>(),
                            storyEventRescheduled = get<StoryEventRescheduledNotifier>(),
                            storyEventNoLongerHappens = get<StoryEventNoLongerHappensNotifier>(),
                            threadTransformer = applicationScope.get()
                        )

                        val view = StoryEventListToolView(
                            this@provide,
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

            provide<CreateStoryEventController.PropertiesPrompt> {
                CreateStoryEventPromptPresenter(get<WorkBench>()::currentStage)
            }

            provide<NormalizationPrompt> {
                NormalizationPromptPresenter(get<WorkBench>()::currentStage)
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
                            val stage =
                                openModal(modality = Modality.APPLICATION_MODAL, owner = get<WorkBench>().currentStage)
                            presenter.viewModel.isCompleted.onChangeUntil({ it == true }) {
                                if (it == true) stage?.hide()
                            }
                        }
                    }
                }
            }

            provide(RescheduleStoryEventPrompt::class, AdjustStoryEventsTimePrompt::class) {
                StoryEventTimeChangePromptPresenter(get<WorkBench>()::currentStage)
            }

            provide<RemoveStoryEventConfirmationPrompt> {
                removeStoryEventConfirmationPrompt(this)
            }
            provide<RemoveCharacterFromStoryEventPromptLocale> {
                applicationScope.get<Locale>().storyEvents.characters.remove.confirmation
            }
            provide<RemoveCharacterFromStoryEventRamificationsReportLocale> {
                applicationScope.get<Locale>().storyEvents.characters.remove.ramifications
            }
            provide<RemoveStoryEventFromStoryRamificationsReportLocale> {
                applicationScope.get<Locale>().storyEvents.remove.ramifications
            }
            provide<RemoveStoryEventConfirmationPromptLocale> {
                applicationScope.get<Locale>().storyEvents.remove.prompt
            }

            provide<StoryEventDetailsDependencies> {
                object : StoryEventDetailsDependencies {
                    override val addCharacterToStoryEventController: AddCharacterToStoryEventController
                        get() = get()
                    override val getStoryEventDetails: GetStoryEventDetails
                        get() = get()
                    override val projectScope: ProjectScope
                        get() = this@provide
                    override val removeCharacterFromStoryEventController: RemoveCharacterFromStoryEventController
                        get() = get()
                    override val asyncScope: CoroutineScope
                        get() = applicationScope
                    override val guiContext: CoroutineContext
                        get() = applicationScope.get<ThreadTransformer>().guiContext
                }
            }

            timeline()
        }
    }

    private fun InProjectScope.timeline() {
        provide<TimelineComponent> {
            val threadTransformer = applicationScope.get<ThreadTransformer>()
            TimelineModule(this, threadTransformer.asyncContext, threadTransformer.guiContext)
        }
        provide { TimelineToolPresenter(this, applicationScope.get(), get(), get()) }
    }

    private class TimelineModule(
        private val projectScope: ProjectScope,
        private val asyncContext: CoroutineContext,
        override val guiContext: CoroutineContext
    ) :
        TimelineComponent,
        TimelineComponent.Dependencies,
        TimelineViewPortComponent.Dependencies,
        TimelineRulerLabelMenuComponent.Dependencies,

        TimelineComponent.GUI,
        TimelineHeaderComponent.Gui,
        TimelineHeaderOptionsButtonComponent.Gui,
        TimelineViewPortComponent.Gui,
        TimelineRulerComponent.Gui,
        TimeSpanLabelComponent.Gui,
        TimelineViewPortGridComponent.Gui,
        StoryPointLabelComponent.GUIComponents,

        StoryPointLabelComponent.Dependencies,
        StoryEventItemMenuComponent.Dependencies {

        override val scope: Scope
            get() = projectScope

        override val listStoryEventsController: ListStoryEventsController
            get() = projectScope.get()

        override val storyEventCreated: Notifier<StoryEventCreatedReceiver>
            get() = projectScope.get<StoryEventCreatedNotifier>()

        override val storyEventNoLongerHappens: Notifier<StoryEventNoLongerHappensReceiver>
            get() = projectScope.get<StoryEventNoLongerHappensNotifier>()

        override val storyEventRenamed: Notifier<StoryEventRenamedReceiver>
            get() = projectScope.get<StoryEventRenamedNotifier>()

        override val storyEventRescheduled: Notifier<StoryEventRescheduledReceiver>
            get() = projectScope.get<StoryEventRescheduledNotifier>()

        override val adjustStoryEventsTimeController: AdjustStoryEventsTimeController
            get() = projectScope.get()

        override val renameStoryEventController: RenameStoryEventController
            get() = projectScope.get()

        override val rescheduleStoryEventController: RescheduleStoryEventController
            get() = projectScope.get()

        override val removeStoryEventController: RemoveStoryEventController
            get() = projectScope.get()

        override fun Timeline(): Timeline {
            return TimelineComponent.Implementation(
                Project.Id(projectScope.projectId),
                this,
                this
            ).Timeline()
        }

        override fun TimelineHeader(
            condensedProperty: BooleanProperty,
            selection: TimelineSelectionModel,
            storyPointLabels: ObservableList<StoryPointLabel>
        ): Node {
            return TimelineHeaderComponent.Implementation(this)
                .TimelineHeader(condensedProperty, selection, storyPointLabels)
        }

        override fun TimelineHeaderCreateButton(): Node {
            return TimelineHeaderCreateButtonComponent.Implementation(projectScope.get()).TimelineHeaderCreateButton()
        }

        override fun TimelineHeaderOptionsButton(
            selection: TimelineSelectionModel,
            storyPointLabels: ObservableList<StoryPointLabel>
        ): Node {
            return TimelineHeaderOptionsButtonComponent.Implementation(this)
                .TimelineHeaderOptionsButton(selection, storyPointLabels)
        }

        override fun TimelineViewPort(storyEventItems: ObservableList<StoryPointLabel>): TimelineViewPort {
            return TimelineViewPortComponent.Implementation(this, this).TimelineViewPort(storyEventItems)
        }

        override fun TimelineRuler(context: TimelineViewportContext): TimelineRuler {
            return TimelineRulerComponent.Implementation(this).TimelineRuler(context)
        }

        override fun TimelineViewPortGrid(viewportContext: TimelineViewportContext): TimelineViewPortGrid {
            return TimelineViewPortGridComponent.Implementation(asyncContext, guiContext, this)
                .TimelineViewPortGrid(viewportContext)
        }

        override fun TimeSpanLabel(
            selection: TimeRangeSelection,
            storyPointLabels: ObservableList<StoryPointLabel>
        ): TimeSpanLabel {
            return TimeSpanLabelComponent.Implementation(this).TimeSpanLabel(selection, storyPointLabels)
        }

        override fun TimelineRulerLabelMenu(
            selection: TimeRangeSelection,
            storyPointLabels: ObservableList<StoryPointLabel>
        ): TimelineRulerLabelMenu {
            return TimelineRulerLabelMenuComponent.Implementation(this)
                .TimelineRulerLabelMenu(selection, storyPointLabels)
        }

        override fun StoryPointLabel(storyEventId: StoryEvent.Id, name: String, time: UnitOfTime): StoryPointLabel {
            return StoryPointLabelComponent.Implementation(this, this).StoryPointLabel(storyEventId, name, time)
        }

        override fun StoryEventItemMenu(selection: StoryEventItemSelection): ContextMenu {
            return StoryEventItemMenuComponent.Implementation(projectScope, this).StoryEventItemMenu(selection)
        }

    }
}