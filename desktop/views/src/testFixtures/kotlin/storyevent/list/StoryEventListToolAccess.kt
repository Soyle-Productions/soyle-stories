package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.list.StoryEventListToolView
import javafx.scene.Node
import javafx.scene.control.*
import tornadofx.Stylesheet

class StoryEventListToolAccess private constructor(private val tool: StoryEventListToolView) : NodeAccess<Node>(tool.root) {
    companion object {
        fun StoryEventListToolView.access() = StoryEventListToolAccess(this)
        fun <T> StoryEventListToolView.drive(op: StoryEventListToolAccess.() -> T): T
        {
            var result: Result<T> = Result.failure(NotImplementedError())
            val access = access()
            access.interact { result = Result.success(access.op()) }
            return result.getOrThrow()
        }
    }

    val createStoryEventButton by temporaryChild<Button>(Stylesheet.button) { it.id == "create-story-event" }
    val storyEventList by temporaryChild<ListView<StoryEventListItemViewModel>>(Stylesheet.listView)
    val storyEventListCells
        get() = storyEventList.findChildren<ListCell<StoryEventListItemViewModel>>(Stylesheet.listCell)
    val storyEventItems: List<StoryEventListItemViewModel>
        get() = storyEventList?.items.orEmpty()

    val retryButton: Button? by temporaryChild<Button>(Stylesheet.button) { it.id == "retry" }

    val optionsButton: MenuButton? by temporaryChild(Stylesheet.menuButton)
    fun MenuButton.insertNewStoryEventOption(placement: String): MenuItem? = items.find { it.id == "insert-story-event-$placement" }
        ?.takeIf { isDisable == false }
    val MenuButton.renameOption: MenuItem?
        get() = items.find { it.id == "rename" }?.takeIf { isDisable == false }
}