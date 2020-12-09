package com.soyle.stories.entities

import com.soyle.stories.common.*
import com.soyle.stories.prose.*
import java.util.*
import kotlin.collections.HashMap

class Prose private constructor(
    override val id: Id,
    val paragraphOrder: List<ProseParagraph.Id>,
    private val paragraphsById: Map<ProseParagraph.Id, ProseParagraph>,
    val revision: Long,

    @Suppress("UNUSED_PARAMETER") defaultConstructorMarker: Unit
) : Entity<Prose.Id> {

    companion object {
        fun create(): ProseUpdate<ProseCreated> {
            val newId = Id()
            val firstParagraph = ProseParagraph.createParagraph(newId, countLines("") as SingleLine)
            val prose =
                Prose(
                    newId,
                    listOf(firstParagraph.paragraph.id),
                    hashMapOf(firstParagraph.paragraph.id to firstParagraph.paragraph),
                    0L,
                    defaultConstructorMarker = Unit
                )
            return prose.updatedBy(ProseCreated(newId, firstParagraph.event))
        }

        fun build(
            id: Id,
            orderedParagraphs: LinkedHashSet<ProseParagraph>,
            revision: Long
        ): Prose {
            if (orderedParagraphs.isEmpty()) error("Prose must have at least one paragraph.")
            if (revision < 0L) error("Revision number must be at least 0.  Got $revision")
            return Prose(
                id,
                orderedParagraphs.map { it.id }.toList(),
                orderedParagraphs.associateByTo(HashMap(orderedParagraphs.size)) { it.id },
                revision,

                defaultConstructorMarker = Unit
            )
        }
    }

    // reads
    val paragraphs: List<ProseParagraph> by lazy {
        paragraphOrder.map { paragraphsById.getValue(it) }
    }

    // updates
    private fun copy(
        paragraphOrder: List<ProseParagraph.Id> = this.paragraphOrder,
        paragraphs: Map<ProseParagraph.Id, ProseParagraph> = this.paragraphsById,
    ) = Prose(id, paragraphOrder, paragraphs, revision = revision + 1L, defaultConstructorMarker = Unit)

    fun withNewParagraphInserted(
        text: SingleLine,
        paragraphIndex: Int
    ): ProseUpdate<ParagraphOrderChanged> {
        val (newParagraph, paragraphCreated) = ProseParagraph.createParagraph(id, text)
        val newOrder = paragraphOrder.plusElementAt(paragraphIndex, newParagraph.id)
        val orderChangedEvent = ParagraphOrderChanged(id, newOrder, paragraphCreated)
        return copy(
            paragraphOrder = newOrder,
            paragraphs = paragraphsById.plus(newParagraph.id to newParagraph)
        ).updatedBy(orderChangedEvent)
    }

    fun withEntityMentioned(
        paragraphId: ProseParagraph.Id,
        entityId: EntityId<*>,
        position: Int,
        length: Int
    ): ProseUpdate<ProseMentionAdded> {
        val paragraph = paragraphsById.getValue(paragraphId)
        return copy(
            paragraphs = paragraphsById.plus(
                paragraph.id to paragraph.withMention(
                    entityId,
                    ProseMentionRange(position, length)
                )
            )
        ).updatedBy(ProseMentionAdded(id, entityId, ProseMentionPosition(paragraphId, position, length)))
    }

    fun withTextInserted(
        text: SingleLine,
        paragraphId: ProseParagraph.Id,
        index: Int? = null
    ): ProseUpdate<ProseParagraphTextModified> {
        val paragraph = paragraphsById.getValue(paragraphId)
        return copy(
            paragraphs = paragraphsById.plus(
                paragraphId to paragraph.withTextInserted(
                    text,
                    index ?: paragraph.content.length
                )
            )
        ).updatedBy(ProseParagraphTextModified(id, text.toString(), listOf()))
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Prose($uuid)"
    }
}

class ProseParagraph private constructor(
    val id: Id,
    val proseId: Prose.Id,
    val content: SingleLine,
    private val mentionsByEntityId: MultiMap<EntityId<*>, ProseMentionRange>
) {

    companion object {
        internal fun createParagraph(
            proseId: Prose.Id,
            initialContent: SingleLine
        ): ProseParagraphUpdate<ParagraphCreated> {
            val newId = Id()
            return ProseParagraph(newId, proseId, initialContent, mapOf()).updatedBy(
                ParagraphCreated(
                    proseId,
                    newId,
                    initialContent.toString()
                )
            )
        }

        fun build(
            id: Id,
            proseId: Prose.Id,
            content: SingleLine,
            mentions: List<ProseMention<*>>
        ): ProseParagraph {
            if (mentions.any { it.position.paragraphId != id }) error("Cannot use mentions from other paragraphs.")
            return ProseParagraph(
                id,
                proseId,
                content,
                mentions.groupBy({ it.entityId }) { it.position.range }
            )
        }
    }

    val allMentions: Sequence<ProseMention<*>>
        get() = mentionsByEntityId.asSequence()
            .flatMap { (k, v) -> v.asSequence().map { ProseMention(k, ProseMentionPosition(id, it)) } }

    private fun copy(
        content: SingleLine = this.content,
        mentions: Map<EntityId<*>, List<ProseMentionRange>> = this.mentionsByEntityId
    ) = ProseParagraph(id, proseId, content, mentions)

    internal fun withMention(entityId: EntityId<*>, range: ProseMentionRange): ProseParagraph {
        if (range.index < 0) throw IndexOutOfBoundsException("Starting position for mention of $entityId is out of bounds.  Got ${range.index}.")
        if (range.index + range.length > content.length) throw IndexOutOfBoundsException("Length of mention of $entityId is too long.  For the index ${range.index}, the maximum length is ${content.length - range.index}.  Got ${range.length}")
        if (allMentions.any { it.position.range.isIntersecting(range) }) {
            throw MentionOverlapsExistingMention()
        }
        return copy(mentions = mentionsByEntityId.plus(entityId, range))
    }

    internal fun withTextInserted(text: SingleLine, index: Int): ProseParagraph {
        if (allMentions.map { it.position.range }.any { index in it.index + 1 until it.index + it.length }) {
            throw ProseMentionCannotBeBisected()
        }
        val newContent = countLines(StringBuilder(content).insert(index, text).toString()) as SingleLine
        val (mentionsAfterIndex, mentionsBeforeIndex) = allMentions.partition { it.position.range.index >= index }
        return if (mentionsAfterIndex.isEmpty()) {
            copy(content = newContent)
        } else {
            copy(
                content = newContent,
                mentions = (mentionsBeforeIndex + mentionsAfterIndex.map { it.shiftedRight(text.length) }).groupBy({ it.entityId }) { it.position.range }
            )
        }
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "ProseParagraph($uuid)"
    }
}

data class ProseMention<Id>(val entityId: EntityId<Id>, val position: ProseMentionPosition) {
    fun shiftedRight(amount: Int) =
        copy(entityId = entityId, position = position.copy(range = position.range.shiftedRight(amount)))

    fun shiftedLeft(amount: Int) =
        copy(entityId = entityId, position = position.copy(range = position.range.shiftedLeft(amount)))
}

data class ProseMentionPosition(val paragraphId: ProseParagraph.Id, val range: ProseMentionRange) {
    constructor(paragraphId: ProseParagraph.Id, index: Int, length: Int) : this(
        paragraphId,
        ProseMentionRange(index, length)
    )
}

data class ProseMentionRange(val index: Int, val length: Int) {
    fun isIntersecting(other: ProseMentionRange): Boolean {
        if (isEmpty() || other.isEmpty()) return false
        return index in other.index until other.index + other.length || other.index in index until index + length
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

class ProseParagraphUpdate<E : ProseEvent?>(val paragraph: ProseParagraph, val event: E) {
    operator fun component2() = event
    operator fun component1() = paragraph
}

fun <E : ProseEvent?> Prose.updatedBy(event: E) = ProseUpdate(this, event)
fun <E : ProseEvent?> ProseParagraph.updatedBy(event: E) = ProseParagraphUpdate(this, event)

