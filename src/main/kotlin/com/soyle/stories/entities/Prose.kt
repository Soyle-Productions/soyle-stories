package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.common.SingleLine
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

    private val sortedMentionSet: Set<ProseMention<*>> by lazy { sortedSetOf(compareBy { it.start() }, *mentions.toTypedArray()) }

    // reads

    // updates
    private fun copy(
        content: String = this.content,
        mentions: List<ProseMention<*>> = this.mentions
    ) = Prose(id, content, mentions, revision = revision + 1L, defaultConstructorMarker = Unit)

    fun withEntityMentioned(
        entityId: MentionedEntityId<*>,
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

    fun withMentionTextReplaced(entityId: MentionedEntityId<*>, newText: String): ProseUpdate<MentionTextReplaced?> {
        val (mentionsOfEntity, mentionsOfOtherEntities) = mentions.partition { it.entityId == entityId }
        if (mentionsOfEntity.isEmpty()) return this.updatedBy(null)

        val lengthDifference = newText.length - mentionsOfEntity.first().position.length
        val contentBuilder = StringBuilder(content)
        mentionsOfEntity.fold(0) { adjustment, mention ->
            contentBuilder.replace(mention.start() + adjustment, mention.end() + adjustment, newText)
            adjustment + lengthDifference
        }

        var adjustment = 0
        val newMentions = sortedMentionSet.map { mention ->
            val shiftedMention = mention.shiftedRight(adjustment)
            if (shiftedMention.entityId == entityId) {
                adjustment += lengthDifference
                shiftedMention.withLength(newText.length)
            } else {
                shiftedMention
            }
        }

        val newProse = copy(content = contentBuilder.toString(), mentions = newMentions)

        return newProse.updatedBy(
            MentionTextReplaced(
                newProse,
                entityId,
                content.substring(mentionsOfEntity.first().start(), mentionsOfEntity.first().end()),
                newText
            )
        )

    }

    fun withTextRemoved(
        range: IntRange
    ): ProseUpdate<TextRemovedFromProse> {
        if (range.first < 0 || range.last > content.length)
            throw IndexOutOfBoundsException("Acceptable range to remove text is 0..${content.length}.  Received $range")
        if (mentions.any { it.position.isBisectedBy(range.first) || it.position.isBisectedBy(range.last+1) }) {
            throw ProseMentionCannotBeBisected()
        }
        val rangeLength = (range.last + 1) - range.first
        val (mentionsBeforeRange, mentionsAfterRangeStart) = mentions.partition { it.position.index < range.first }
        val (mentionsInRange, mentionsAfterRange) = mentionsAfterRangeStart.partition { it.end() < range.last }

        val newProse = copy(
            content = StringBuilder(content).removeRange(range).toString(),
            mentions = mentionsBeforeRange + mentionsAfterRange.map { it.shiftedLeft(rangeLength) }
        )
        return newProse.updatedBy(TextRemovedFromProse(newProse, content.substring(range), range.first))
    }

    fun withoutMention(
        mention: ProseMention<*>
    ): ProseUpdate<MentionRemovedFromProse> {
        if (!sortedMentionSet.contains(mention)) throw MentionDoesNotExistInProse(id, mention)
        val newProse = copy(
            mentions = mentions.minus(mention)
        )
        return newProse.updatedBy(MentionRemovedFromProse(newProse, mention.entityId, mention.position))
    }

    fun withContentReplaced(content: List<ProseContent>): ProseUpdate<ContentReplaced> {
        var offset = 0
        val newMentions = content.mapNotNull { (leadingText, mention) ->
            if (mention == null) return@mapNotNull null
            offset += leadingText.length
            ProseMention(mention.first, ProseMentionRange(offset, mention.second.length)).also {
                offset += mention.second.length
            }
        }
        val newProse = copy(
            content = content.joinToString("") { proseContent ->
                proseContent.text + (proseContent.mention?.second ?: "")
            },
            mentions = newMentions
        )
        return newProse.updatedBy(ContentReplaced(newProse))
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Prose($uuid)"
    }
}

data class ProseContent(val text: String, val mention: Pair<MentionedEntityId<*>, SingleLine>?)

sealed class MentionedEntityId<Id> {
    abstract val id: Id
}

data class MentionedCharacterId(override val id: Character.Id) : MentionedEntityId<Character.Id>()
data class MentionedLocationId(override val id: Location.Id) : MentionedEntityId<Location.Id>()

fun Character.Id.mentioned() = MentionedCharacterId(this)
fun Location.Id.mentioned() = MentionedLocationId(this)

data class ProseMention<Id>(val entityId: MentionedEntityId<Id>, val position: ProseMentionRange) {
    fun start(): Int = position.index
    fun end(): Int = position.index + position.length

    fun shiftedRight(amount: Int) =
        copy(entityId = entityId, position = position.shiftedRight(amount))

    fun shiftedLeft(amount: Int) =
        copy(entityId = entityId, position = position.shiftedLeft(amount))

    fun withLength(length: Int) =
        copy(entityId = entityId, position = position.withLength(length))
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
    fun withLength(length: Int) = copy(length = length)
}

class ProseUpdate<E : ProseEvent?>(val prose: Prose, val event: E) {
    operator fun component2() = event
    operator fun component1() = prose
}

fun <E : ProseEvent?> Prose.updatedBy(event: E) = ProseUpdate(this, event)

