package com.soyle.stories.desktop.view.prose.proseEditor

import com.soyle.stories.prose.proseEditor.*
import javafx.geometry.Point2D
import javafx.scene.control.ContextMenu
import javafx.scene.control.ListView
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.stage.Popup
import org.testfx.api.FxRobot

class ProseEditorDriver(private val proseEditor: ProseEditorView) : FxRobot() {

    companion object {
        inline fun ProseEditorView.drive(crossinline driving: ProseEditorDriver.() -> Unit = {}) =
            driver().apply { interact { driving() } }

        fun ProseEditorView.driver() = ProseEditorDriver(this)
    }

    val textArea
        get() = proseEditor.root.childrenUnmodifiable.first() as ProseEditorTextArea
    private val mentionMenu: Popup
        get() = proseEditor.properties["mentionMenu"] as Popup
    val mentionMenuList: ListView<MatchingStoryElementViewModel>
        get() = from(mentionMenu.scene.root).lookup(".list-view").query()
    val mentionMenuItems
        get() = mentionMenuList.items

    fun getContent(): String {
        return textArea.content.text
    }

    fun getMentionAt(start: Int, end: Int): Mention? {
        var offset = 0
        return textArea.paragraphs.asSequence()
            .flatMap { it.segments.asSequence() }
            .find {
                if (it !is Mention) {
                    offset += it.text.length
                    return@find false
                }
                if (offset == start && (offset + it.text.length) == end) true
                else {
                    offset += it.text.length
                    false
                }
            } as? Mention
    }


    fun isShowingMentionMenu(): Boolean {
        return mentionMenu.isShowing
    }

    fun mentionMenuCharacterAlignment(): Int? {
        val localPosition = textArea.screenToLocal(mentionMenu.x, mentionMenu.y) ?: return null
        if (localPosition.x.isNaN() || localPosition.y.isNaN()) return null
        val characterIndex = textArea.hit(localPosition.x, localPosition.y)?.characterIndex ?: return null
        if (characterIndex.isEmpty) return null
        return characterIndex.asInt
    }

    fun listedStoryElementAt(index: Int): MatchingStoryElementViewModel? {
        return from(mentionMenu.scene.root)
            .lookup(".list-view")
            .query<ListView<MatchingStoryElementViewModel>>()
            .items.getOrNull(index)
    }

    fun positionOfMentionWithText(mentionText: String): Point2D
    {
        val start = textArea.text.indexOf(mentionText)
        val mentionBounds = textArea.getCharacterBoundsOnScreen(start, start + mentionText.length).get()
        return Point2D(mentionBounds.minX + 2, mentionBounds.minY + 2)
    }

    val mentionIssueMenu: ContextMenu?
        get() = textArea.contextMenu

    fun isShowingMentionIssueMenu(): Boolean = mentionIssueMenu?.isShowing == true
    fun mentionIssueMenuIsRelatedToMention(mentionText: String): Boolean = mentionIssueMenu?.properties?.get("relative-mention")?.let {
        it is Mention && it.text == mentionText
    } == true

    fun ContextMenu.clearMentionOption(): MenuItem? = items.find { it.id == "clear-mention" }
    fun ContextMenu.removeMentionOption(): MenuItem? = items.find { it.id == "remove-mention" }
    fun ContextMenu.replacementOption(): Menu? = items.find { it.id == "replace-mention" } as? Menu

}