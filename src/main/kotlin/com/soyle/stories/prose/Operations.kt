package com.soyle.stories.prose

import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseParagraph

sealed class Operation {
    abstract val proseId: Prose.Id
    abstract val paragraphId: ProseParagraph.Id
    abstract fun inverse(): Operation?

}

class Insert(
    override val proseId: Prose.Id,
    override val paragraphId: ProseParagraph.Id,
    val text: String,
    val position: UInt
) : Operation() {
    override fun inverse(): Delete = Delete(proseId, paragraphId, position+ text.length.toUInt(), text)
}

class Delete(
    override val proseId: Prose.Id,
    override val paragraphId: ProseParagraph.Id,
    val position: UInt,
    val expectedText: String
) : Operation() {
    override fun inverse(): Insert = Insert(proseId, paragraphId, expectedText, position - expectedText.length.toUInt())
}

