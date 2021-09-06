package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.storyevent.list.StoryEventListItemViewModel
import com.soyle.stories.storyevent.list.StoryEventListToolView
import javafx.scene.Node
import javafx.scene.control.*
import tornadofx.CssRule
import tornadofx.Stylesheet
import tornadofx.cssclass

class StoryEventListToolAccess private constructor(private val tool: StoryEventListToolView) : NodeAccess<Node>(tool) {
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
    fun MenuButton.insertNewStoryEventOption(placement: String): MenuItem? = (items.find { it.id == "insert" } as? Menu)
        ?.takeIf { isDisable == false }
        ?.items?.find { it.id == placement }
        ?.takeIf { isDisable == false }

    val MenuButton.renameOption: MenuItem?
        get() = items.find { it.id == "rename" }?.takeIf { isDisable == false }
    val MenuButton.rescheduleOption: MenuItem?
        get() = items.find { it.id == "reschedule" }?.takeIf { isDisable == false }
    val MenuButton.adjustTimeOption: MenuItem?
        get() = items.find { it.id == "adjust" }?.takeIf { isDisable == false }
    val MenuButton.deleteOption: MenuItem?
        get() = items.find { it.id == "delete" }?.takeIf { isDisable == false }

    val ListCell<StoryEventListItemViewModel>.nameLabel: Label?
        get() = findChild(CssRule.c("name"))
    val ListCell<StoryEventListItemViewModel>.timeLabel: Label?
        get() = findChild(CssRule.c("time"))

}