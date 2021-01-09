package com.soyle.stories.prose

import com.soyle.stories.common.EntityId
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseMentionRange

sealed class ProseEvent(prose: Prose) {
    val proseId: Prose.Id = prose.id
    val revision: Long = prose.revision
}

class ProseCreated(prose: Prose) : ProseEvent(prose)
class TextInsertedIntoProse(
    prose: Prose,
    val insertedText: String,
    val index: Int
) : ProseEvent(prose)

class TextRemovedFromProse(
    prose: Prose,
    val deletedText: String,
    val index: Int
) : ProseEvent(prose) {
    val newMentions = prose.mentions
}

class EntityMentionedInProse(
    prose: Prose,
    val entityId: EntityId<*>,
    val position: ProseMentionRange
) : ProseEvent(prose)

class MentionRemovedFromProse(
    prose: Prose,
    val entityId: EntityId<*>,
    val position: ProseMentionRange
) : ProseEvent(prose)

class MentionTextReplaced(
    prose: Prose,
    val entityId: EntityId<*>,
    val deletedText: String,
    val insertedText: String
) : ProseEvent(prose) {
    val newContent = prose.content
    val newMentions = prose.mentions
}

class ContentReplaced(
    prose: Prose
) : ProseEvent(prose)
{
    val newContent = prose.content
    val newMentions = prose.mentions
}