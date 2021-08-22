package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.layout.config.fixed.StoryEventList
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.list.creationButton.StoryEventListTool
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import tornadofx.Stylesheet

class StoryEventListToolAccess private constructor(private val tool: StoryEventListTool) : NodeAccess<Node>(tool.root) {
    companion object {
        fun StoryEventListTool.access() = StoryEventListToolAccess(this)
        fun <T> StoryEventListTool.drive(op: StoryEventListToolAccess.() -> T): T
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

}