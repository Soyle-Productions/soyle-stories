package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.common.EntityId
import com.soyle.stories.prose.*
import java.util.*

class Prose private constructor(
    override val id: Id,
    val content: String,
    val mentions: List<ProseMention<*>>,
    val revision: Long,

    @Suppress("UNUSED_PARAMETER") defaultConstructorMarker: Unit
) : Entity<Prose.Id> {

    companion object {
        fun create(): ProseUpdate<ProseCreated> {
            val newId = Id()
            val prose =
                Prose(
                    newId,
                    "",
                    listOf(),
                    0L,
                    defaultConstructorMarker = Unit
                )
            return prose.updatedBy(ProseCreated(prose))
        }

        fun build(
            id: Id,
            content: String,
            mentions: List<ProseMention<*>>,
            revision: Long
        ): Prose {
            if (revision < 0L) error("Revision number must be at least 0.  Got $revision")
            val sortedMentions = mentions.sortedBy { it.position.index }
            sortedMentions.forEachIndexed { index, proseMention ->
                sortedMentions.subList(index + 1, sortedMentions.size).forEach {
                    if (it.position.isIntersecting(proseMention.position)) error("No two mentions can intersect.  $it, $proseMention")
                }
            }
            return Prose(
                id,
                content,
                mentions,
                revision,

                defaultConstructorMarker = Unit
            )
        }
    }

    // reads

    // updates
    private fun copy(
        content: String = this.content,
        mentions: List<ProseMention<*>> = this.mentions
    ) = Prose(id, content, mentions, revision = revision + 1L, defaultConstructorMarker = Unit)

    fun withEntityMentioned(
        entityId: EntityId<*>,
        position: Int,
        length: Int
    ): ProseUpdate<EntityMentionedInProse> {
        val range = ProseMentionRange(position, length)
        if (position < 0) throw IndexOutOfBoundsException(position)
        if (position + length > content.length) throw IndexOutOfBoundsException(position + length)
        if (mentions.any { it.position.isIntersecting(range) }) {
            throw MentionOverlapsExistingMention()
        }
        val newProse = copy(
            mentions = mentions + ProseMention(entityId, range)
        )
        return newProse.updatedBy(EntityMentionedInProse(newProse, entityId, range))
    }

    fun withTextInserted(
        text: String,
        index: Int? = null
    ): ProseUpdate<TextInsertedIntoProse> {
        val insertIndex = index ?: content.length
        if (mentions.any { it.position.isBisectedBy(insertIndex) }) {
            throw ProseMentionCannotBeBisected()
        }
        val (mentionsBeforeIndex, mentionsAfterIndex) = mentions.partition { it.position.index < insertIndex }
        val newProse = copy(
            content = StringBuilder(content).insert(insertIndex, text).toString(),
            mentions = mentionsBeforeIndex + mentionsAfterIndex.map { it.shiftedRight(text.length) }
        )
        return newProse.updatedBy(TextInsertedIntoProse(newProse, text, insertIndex))
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Prose($uuid)"
    }
}

data class ProseMention<Id>(val entityId: EntityId<Id>, val position: ProseMentionRange) {
    fun shiftedRight(amount: Int) =
        copy(entityId = entityId, position = position.shiftedRight(amount))

    fun shiftedLeft(amount: Int) =
        copy(entityId = entityId, position = position.shiftedLeft(amount))
}

data class ProseMentionRange(val index: Int, val length: Int) {
    fun isIntersecting(other: ProseMentionRange): Boolean {
        if (isEmpty() || other.isEmpty()) return false
        return index in other.index until other.index + other.length || other.index in index until index + length
    }
    inline fun isBisectedBy(index: Int): Boolean {
        return index in this.index + 1 until this.index + this.length
    }

    fun isEmpty() = length == 0
    operator fun contains(index: Int) = index in this.index..(this.index + length)

    fun shiftedRight(amount: Int) = copy(index = index + amount)
    fun shiftedLeft(amount: Int) = copy(index = index - amount)
}

class ProseUpdate<E : ProseEvent?>(val prose: Prose, val event: E) {
    operator fun component2() = event
    operator fun component1() = prose
}

fun <E : ProseEvent?> Prose.updatedBy(event: E) = ProseUpdate(this, event)

