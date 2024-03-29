package com.soyle.stories.domain.prose.events

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.Prose

class MentionTextReplaced(
    prose: Prose,
    val entityId: MentionedEntityId<*>,
    val deletedText: String,
    val insertedText: String
) : ProseEvent(prose) {
    val newContent = prose.text
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