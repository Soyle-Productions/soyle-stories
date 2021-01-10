package com.soyle.stories.prose

import com.soyle.stories.entities.MentionedEntityId
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.entities.ProseMentionRange
import org.junit.jupiter.api.Assertions.assertEquals

fun makeProse(
    id: Prose.Id = Prose.Id(),
    content: String = "",
    mentions: List<ProseMention<*>> = listOf(),
    revision: Long = LongRange(0L, Long.MAX_VALUE).random()
): Prose {
    return Prose.build(
        id,
        content,
        mentions,
        revision
    )
}

fun textInsertedIntoProse(proseId: Prose.Id, revision: Long, insertedText: String, index: Int) =
    fun(event: ProseEvent) {
        event as TextInsertedIntoProse
        assertEquals(proseId, event.proseId)
        assertEquals(revision, event.revision)
        assertEquals(insertedText, event.insertedText)
        assertEquals(index, event.index)
    }

fun entityMentionedInProse(proseId: Prose.Id, revision: Long, entityId: MentionedEntityId<*>, range: ProseMentionRange) =
    fun(event: ProseEvent) {
        event as EntityMentionedInProse
        assertEquals(proseId, event.proseId)
        assertEquals(revision, event.revision)
        assertEquals(entityId, event.entityId)
        assertEquals(range, event.position)
    }

fun ProseEvent.mustMatch(expectation: (ProseEvent) -> Unit) = expectation(this)