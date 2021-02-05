package com.soyle.stories.prose.proseEditor

import org.fxmisc.richtext.model.TextOps
import java.util.*

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