package com.soyle.stories.domain.prose

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
    val entityId: MentionedEntityId<*>,
    val position: ProseMentionRange
) : ProseEvent(prose)

class MentionRemovedFromProse(
    prose: Prose,
    val entityId: MentionedEntityId<*>,
    val position: ProseMentionRange
) : ProseEvent(prose)

class MentionTextReplaced(
    prose: Prose,
    val entityId: MentionedEntityId<*>,
    val deletedText: String,
    val insertedText: String
) : ProseEvent(prose) {
    val newContent = prose.content
    val newMentions = prose.mentions

    override fun toString(): String {
        return "MentionTextReplaced(entityId=$entityId, deletedText=$deletedText, insertedText=$insertedText, newContent=$newContent, newMentions=$newMentions)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MentionTextReplaced

        if (entityId != other.entityId) return false
        if (deletedText != other.deletedText) return false
        if (insertedText != other.insertedText) return false
        if (newContent != other.newContent) return false
        if (newMentions != other.newMentions) return false

        return true
    }

    private val _hashCode: Int by lazy {
        listOf(
            deletedText,
            insertedText,
            newContent,
            newMentions
        ).fold(entityId.hashCode()) { a, b -> 31 * a + b.hashCode() }
    }

    override fun hashCode(): Int = _hashCode


}

class ContentReplaced(
    prose: Prose
) : ProseEvent(prose)
{
    val newContent = prose.content
    val newMentions = prose.mentions
}