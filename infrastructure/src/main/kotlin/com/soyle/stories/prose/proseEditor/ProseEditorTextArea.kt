package com.soyle.stories.prose.proseEditor

import com.soyle.stories.prose.proseEditor.ProseEditorTextArea.Styles.Companion.mention
import com.soyle.stories.prose.proseEditor.ProseEditorTextArea.Styles.Companion.problem
import com.soyle.stories.prose.proseEditor.ProseEditorTextArea.Styles.Companion.proseEditorTextArea
import javafx.scene.paint.Color
import org.fxmisc.richtext.GenericStyledArea
import org.fxmisc.richtext.TextExt
import org.fxmisc.richtext.model.TextOps
import tornadofx.*
import java.util.*
import com.soyle.stories.soylestories.Styles as SoyleStyles

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
) {

    init {
        addClass(proseEditorTextArea)
    }
    class Styles : Stylesheet()
    {

        companion object {

            val proseEditorTextArea by cssclass()
            val mention by cssclass()
            val problem by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            mention {
                val transparentHighlight = Color.rgb(
                    (SoyleStyles.Blue.red * 255).toInt(),
                    (SoyleStyles.Blue.green * 255).toInt(),
                    (SoyleStyles.Blue.blue * 255).toInt(),
                    0.2,
                )
                unsafe("-rtfx-background-color", raw(transparentHighlight.css))
                fill = SoyleStyles.Blue
            }
            problem {
                val transparentHighlight = Color.rgb(
                    (SoyleStyles.Orange.red * 255).toInt(),
                    (SoyleStyles.Orange.green * 255).toInt(),
                    (SoyleStyles.Orange.blue * 255).toInt(),
                    0.2,
                )
                unsafe("-rtfx-background-color", raw(transparentHighlight.css))
                fill = SoyleStyles.Orange
            }
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