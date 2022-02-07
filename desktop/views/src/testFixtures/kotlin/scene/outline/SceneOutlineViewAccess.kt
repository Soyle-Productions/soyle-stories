package com.soyle.stories.desktop.view.scene.outline

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.outline.SceneOutlineStyles
import com.soyle.stories.scene.outline.SceneOutlineView
import com.soyle.stories.scene.outline.item.OutlinedStoryEventItem
import com.soyle.stories.scene.outline.item.OutlinedStoryEventItemView
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.MenuButton
import tornadofx.CssRule
import tornadofx.Rendered
import tornadofx.Stylesheet

class SceneOutlineViewAccess private constructor(private val view: SceneOutlineView) :
    NodeAccess<SceneOutlineView>(view) {
    companion object {
        fun SceneOutlineView.access(): SceneOutlineViewAccess = SceneOutlineViewAccess(this)
        fun <T> SceneOutlineView.drive(op: SceneOutlineViewAccess.() -> T): T {
            var result: Result<T>? = null
            with(access()) {
                interact {
                    result = kotlin.runCatching { op() }
                }
            }
            return result!!.getOrThrow()
        }
    }

    fun isFocusedOn(scene: Scene): Boolean {
        return view.focusedSceneId == scene.id
    }

    val additionMenu: MenuButton? by temporaryChild(object : Rendered {
        override fun render() = "#cover-story-event"
    })
    private val list: ListView<OutlinedStoryEventItem>? by temporaryChild(CssRule.c("list-view"))

    fun getListedStoryEvent(storyEventId: StoryEvent.Id): OutlinedStoryEventItemView? {
        val list = list ?: return null
        return list.findChild(SceneOutlineStyles.outlinedEvent) { it.id == storyEventId.toString() }
    }

    fun getStoryEvent(storyEventId: StoryEvent.Id): OutlinedStoryEventItem? {
        val list = list ?: return null
        return list.items.find { it.storyEventId == storyEventId }
    }

    fun getStoryEventByName(name: String): OutlinedStoryEventItem? {
        val list = list ?: return null
        return list.items.find { it.name == name }
    }

    val OutlinedStoryEventItemView.optionsMenu: MenuButton
        get() = findChild(CssRule.c("menu-button"))!!

}