package com.soyle.stories.storyevent.item

import com.soyle.stories.di.get
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import javafx.scene.control.ContextMenu
import tornadofx.*


class StoryEventItemMenuViewModel(
    val selection: StoryEventItemSelection
)

interface StoryEventItemMenuActions {

    fun renameSelectedItem()
    fun rescheduleSelectedItem()
    fun adjustTimesOfSelectedItems()
    fun deleteSelectedItems()

}

fun storyEventItemMenu(
    viewModel: StoryEventItemMenuViewModel,
    actions: StoryEventItemMenuActions
) = ContextMenu().apply {
    item("Rename") {
        id = "rename"
        enableWhen(viewModel.selection.hasSingleSelection())
        action(actions::renameSelectedItem)
    }
    item("Reschedule") {
        id = "reschedule"
        enableWhen(viewModel.selection.hasSingleSelection())
        action(actions::rescheduleSelectedItem)
    }
    item("Adjust Time") {
        id = "adjust"
        action(actions::adjustTimesOfSelectedItems)
    }
    item("Delete") {
        id = "delete"
        action(actions::deleteSelectedItems)
    }
}

class StoryEventItemMenuPresenter(
    private val scope: Scope,
    private val viewModel: StoryEventItemMenuViewModel,
    private val dependencies: StoryEventItemMenuComponent.Dependencies
) : StoryEventItemMenuActions {
    override fun renameSelectedItem() {
        singleSelection {
            dependencies.renameStoryEventController.requestToRenameStoryEvent(it.storyEventId, it.name)
        }
    }

    override fun rescheduleSelectedItem() {
        singleSelection {
            dependencies.rescheduleStoryEventController.rescheduleStoryEvent(it.storyEventId)
        }
    }

    override fun adjustTimesOfSelectedItems() {
        val selectedItems = viewModel.selection.selectedIds
        dependencies.adjustStoryEventsTimeController.adjustTimes(selectedItems)
    }

    override fun deleteSelectedItems() {
        val selectedItems = viewModel.selection.selectedIds
        dependencies.removeStoryEventController.removeStoryEvent(
            selectedItems,
            scope.get(),
            scope.get()
        )
    }

    private inline fun singleSelection(op: (StoryEventItemViewModel) -> Unit) {
        viewModel.selection.selectedItems.singleOrNull()?.let(op)
    }
}

@Suppress("FunctionName")
interface StoryEventItemMenuComponent {
    fun StoryEventItemMenu(selection: StoryEventItemSelection): ContextMenu

    interface Dependencies {
        val renameStoryEventController: RenameStoryEventController
        val rescheduleStoryEventController: RescheduleStoryEventController
        val adjustStoryEventsTimeController: AdjustStoryEventsTimeController
        val removeStoryEventController: RemoveStoryEventController
    }

    companion object {
        fun Implementation(
            scope: Scope,
            dependencies: Dependencies
        ) = object : StoryEventItemMenuComponent {
            override fun StoryEventItemMenu(selection: StoryEventItemSelection): ContextMenu {
                val viewModel = StoryEventItemMenuViewModel(selection)
                return storyEventItemMenu(
                    viewModel,
                    StoryEventItemMenuPresenter(scope, viewModel, dependencies)
                )
            }
        }
    }

}