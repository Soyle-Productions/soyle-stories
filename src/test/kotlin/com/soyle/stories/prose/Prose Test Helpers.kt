package com.soyle.stories.prose

import com.soyle.stories.common.SingleLine
import com.soyle.stories.common.singleLine
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.entities.ProseParagraph

fun makeProse(
    id: Prose.Id = Prose.Id(),
    paragraphs: List<ProseParagraph> = listOf(makeProseParagraph(proseId = id)),
    revision: Long = LongRange(0L, Long.MAX_VALUE).random()
): Prose
{
    return Prose.build(
        id,
        paragraphs.mapTo(LinkedHashSet(paragraphs.size)) { it },
        revision
    )
}

fun makeProseParagraph(
    id: ProseParagraph.Id = ProseParagraph.Id(),
    proseId: Prose.Id = Prose.Id(),
    content: SingleLine = singleLine(""),
    mentions: List<ProseMention<*>> = listOf()
): ProseParagraph
{
    return ProseParagraph.build(
        id,
        proseId,
        content,
        mentions
    )
}