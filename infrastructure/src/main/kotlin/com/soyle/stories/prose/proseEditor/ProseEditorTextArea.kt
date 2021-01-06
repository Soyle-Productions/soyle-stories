package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.prose.proseEditor.ProseEditorTextAreaStyles.Companion.mention
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Text
import org.fxmisc.richtext.GenericStyledArea
import org.fxmisc.richtext.model.TextOps
import tornadofx.*
import java.util.*

class ProseEditorTextArea : GenericStyledArea<Unit, ContentElement, Collection<String>>(
    /*initialParagraphStyle = */Unit,
    /*applyParagraphStyle =  */{ _, _ -> },
    /*initialTextStyle =  */listOf(""),
    /*segmentOps =  */ContentElementOps(),
    /*nodeFactory =  */{
        Text(it.segment.text).apply {
            if (it.segment is Mention) {
                addClass(mention)
            }
        }
    }
)

class ProseEditorTextAreaStyles : Stylesheet()
{

    companion object {

        val mention by cssclass()

        init {
            importStylesheet<ProseEditorTextAreaStyles>()
        }
    }

    init {
        mention {
            backgroundColor += Color.BLUE
        }
    }

}

class ContentElementOps : TextOps<ContentElement, Collection<String>> {
    override fun length(seg: ContentElement?): Int = seg!!.text.length ?: 0

    override fun charAt(seg: ContentElement?, index: Int): Char = seg!!.text[index]

    override fun getText(seg: ContentElement?): String = seg!!.text

    override fun subSequence(seg: ContentElement?, start: Int, end: Int): ContentElement {
        return when (seg)
        {
            is BasicText -> BasicText(seg.text.substring(start, end))
            is Mention -> seg
            null -> BasicText("")
        }
    }

    override fun subSequence(seg: ContentElement?, start: Int): ContentElement {
        return when (seg)
        {
            is BasicText -> BasicText(seg.text.substring(start))
            is Mention -> seg
            null -> BasicText("")
        }
    }

    override fun joinSeg(currentSeg: ContentElement?, nextSeg: ContentElement?): Optional<ContentElement> {
        if (currentSeg == null || nextSeg == null) return Optional.empty()
        return when (currentSeg)
        {
            is BasicText -> if (nextSeg is BasicText) Optional.of(BasicText(currentSeg.text + nextSeg.text)) else Optional.empty<ContentElement>()
            is Mention, null -> Optional.empty<ContentElement>()
        }
    }

    override fun createEmptySeg(): ContentElement = BasicText("")

    override fun create(text: String?): ContentElement = BasicText(text ?: "")

}