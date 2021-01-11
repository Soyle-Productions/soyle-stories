package com.soyle.stories.prose.proseEditor

import com.soyle.stories.prose.proseEditor.ProseEditorTextAreaStyles.Companion.mention
import com.soyle.stories.prose.proseEditor.ProseEditorTextAreaStyles.Companion.problem
import com.soyle.stories.soylestories.Styles
import javafx.scene.paint.Color
import org.fxmisc.richtext.GenericStyledArea
import org.fxmisc.richtext.TextExt
import org.fxmisc.richtext.model.TextOps
import tornadofx.*
import java.util.*

class ProseEditorTextArea : GenericStyledArea<Unit, ContentElement, Collection<String>>(
    /*initialParagraphStyle = */Unit,
    /*applyParagraphStyle =  */{ _, _ -> },
    /*initialTextStyle =  */listOf(""),
    /*segmentOps =  */ContentElementOps(),
    /*nodeFactory =  */{
        val segment = it.segment
        TextExt(segment.text).apply {
            if (segment is Mention) {
                toggleClass(mention, segment.issue == null)
                toggleClass(problem, segment.issue != null)
            }
        }
    }
)

class ProseEditorTextAreaStyles : Stylesheet()
{

    companion object {

        val mention by cssclass()
        val problem by cssclass()

        init {
            importStylesheet<ProseEditorTextAreaStyles>()
        }
    }

    init {
        mention {
            val transparentHighlight = Color.rgb(
                (Styles.Blue.red * 255).toInt(),
                (Styles.Blue.green * 255).toInt(),
                (Styles.Blue.blue * 255).toInt(),
                0.2,
            )
            unsafe("-rtfx-background-color", raw(transparentHighlight.css))
            fill = Styles.Blue
        }
        problem {
            val transparentHighlight = Color.rgb(
                (Styles.Orange.red * 255).toInt(),
                (Styles.Orange.green * 255).toInt(),
                (Styles.Orange.blue * 255).toInt(),
                0.2,
            )
            unsafe("-rtfx-background-color", raw(transparentHighlight.css))
            fill = Styles.Orange
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
            is Mention -> seg.copy(text = seg.text.substring(start, end))
            null -> BasicText("")
        }
    }

    override fun subSequence(seg: ContentElement?, start: Int): ContentElement {
        return when (seg)
        {
            is BasicText -> BasicText(seg.text.substring(start))
            is Mention -> seg.copy(text = seg.text.substring(start))
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