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

class EntityMentionedInProse(
    prose: Prose,
    val entityId: EntityId<*>,
    val position: ProseMentionRange
) : ProseEvent(prose)

class ContentReplaced(
    prose: Prose
) : ProseEvent(prose)
{
    val newContent = prose.content
    val newMentions = prose.mentions
}