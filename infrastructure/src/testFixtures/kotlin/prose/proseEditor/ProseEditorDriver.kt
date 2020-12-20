package com.soyle.stories.desktop.view.prose.proseEditor

import com.soyle.stories.common.EntityId
import com.soyle.stories.di.get
import com.soyle.stories.prose.proseEditor.MatchingStoryElementViewModel
import com.soyle.stories.prose.proseEditor.ProseEditorState
import com.soyle.stories.prose.proseEditor.ProseEditorView
import javafx.scene.control.ListView
import javafx.stage.Popup
import org.fxmisc.richtext.StyleClassedTextArea
import org.testfx.api.FxRobot

class ProseEditorDriver(private val proseEditor: ProseEditorView) : FxRobot() {

    companion object {
        inline fun ProseEditorView.drive(crossinline driving: ProseEditorDriver.() -> Unit = {}) = driver().apply { interact { driving() } }
        fun ProseEditorView.driver() = ProseEditorDriver(this)
    }

    val textArea
        get() = proseEditor.root as StyleClassedTextArea
    private val mentionMenu: Popup
        get() =proseEditor.properties["mentionMenu"] as Popup

    fun getContent(): String
    {
        return textArea.content.text
    }

    fun getMentionAt(start: Int, end: Int): EntityId<*>? = textArea.getStyleOfChar(start).firstOrNull()?.let { idText ->
        proseEditor.scope.get<ProseEditorState>().mentions.find {
            it.entityId.id.toString() == idText
        }?.entityId
    }

    fun isShowingMentionMenu(): Boolean
    {
        return mentionMenu.isShowing
    }

    fun mentionMenuCharacterAlignment(): Int?
    {
        val localPosition = textArea.screenToLocal(mentionMenu.x, mentionMenu.y) ?: return null
        if (localPosition.x.isNaN() || localPosition.y.isNaN()) return null
        val characterIndex = textArea.hit(localPosition.x, localPosition.y)?.characterIndex ?: return null
        if (characterIndex.isEmpty) return null
        return characterIndex.asInt
    }

    fun listedStoryElementAt(index: Int): MatchingStoryElementViewModel?
    {
        return from(mentionMenu.scene.root)
            .lookup(".list-view")
            .query<ListView<MatchingStoryElementViewModel>>()
            .items.getOrNull(index)
    }

}