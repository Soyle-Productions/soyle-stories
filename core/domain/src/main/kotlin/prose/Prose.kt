package com.soyle.stories.domain.prose

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.prose.events.*
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.domain.validation.countLines
import java.util.*

class Prose private constructor(
    override val id: Id,
    val projectId: Project.Id,
    val text: String,
    private val _mentions: List<ProseMention<*>>,
    val revision: Long,

    @Suppress("UNUSED_PARAMETER") defaultConstructorMarker: Unit
) : Entity<Prose.Id> {

    companion object {
        fun create(projectId: Project.Id): ProseUpdate<ProseCreated> {
            val prose = Prose(Id(), projectId, "", listOf(), 0L, defaultConstructorMarker = Unit)
            return prose.updatedBy(ProseCreated(prose))
        }

        fun build(
            id: Id,
            projectId: Project.Id,
            content: List<ProseContent>,
            revision: Long
        ): Prose {
            if (revision < 0L) error("Revision number must be at least 0.  Got $revision")

            val text = content.joinToString("") {  proseContent ->
                proseContent.text + (proseContent.mention?.second ?: "")
            }

            var offset = 0
            val newMentions = content.mapNotNull { (leadingText, mention) ->
                if (mention == null) return@mapNotNull null
                offset += leadingText.length
                ProseMention(mention.first, ProseMentionRange(offset, mention.second.length), text).also {
                    offset += mention.second.length
                }
            }

            return Prose(
                id,
                projectId,
                text,
                newMentions,
                revision,

                defaultConstructorMarker = Unit
            )
        }
    }


    // reads

    private val sortedMentionSet: Set<ProseMention<*>> by lazy { sortedSetOf(compareBy { it.startIndex }, *_mentions.toTypedArray()) }

    val content: List<com.soyle.stories.domain.prose.content.ProseContent> by lazy {
        if (text.isEmpty()) emptyList()
        else {
            if (sortedMentionSet.isEmpty()) return@lazy listOf(ProseText(text, 0, text.length))
            var offset = 0
            sortedMentionSet.flatMap {
                listOfNotNull(
                    ProseText(text.substring(offset, it.startIndex), offset, it.startIndex).takeIf { it.text.isNotEmpty() },
                    it
                ).also { _ -> offset = it.endIndex }
            } + listOfNotNull(
                ProseText(text.substring(offset, text.length), offset, text.length).takeIf { it.text.isNotEmpty() }
            )

        }
    }
    val mentions: List<com.soyle.stories.domain.prose.content.ProseContent.Mention<*>>
        get() = _mentions

    private val mentionsByEntityId: Map<MentionedEntityId<*>, List<com.soyle.stories.domain.prose.content.ProseContent.Mention<*>>> by lazy { mentions.groupBy { it.entityId } }

    fun containsMentionOf(entityId: MentionedEntityId<*>): Boolean = mentionsByEntityId.containsKey(entityId)

    // updates
    private fun copy(
        text: String = this.text,
        mentions: List<ProseMention<*>> = this._mentions
    ) = Prose(id, projectId, text, mentions, revision = revision + 1L, defaultConstructorMarker = Unit)

    fun withEntityMentioned(
        entityId: MentionedEntityId<*>,
        position: Int,
        length: Int
    ): ProseUpdate<EntityMentionedInProse> {
        val range = ProseMentionRange(position, length)
        if (position < 0) throw IndexOutOfBoundsException(position)
        if (position + length > text.length) throw IndexOutOfBoundsException(position + length)
        if (_mentions.any { it.position.isIntersecting(range) }) {
            throw MentionOverlapsExistingMention()
        }
        val newProse = copy(
            mentions = _mentions + ProseMention(entityId, range, text)
        )
        return newProse.updatedBy(EntityMentionedInProse(newProse, entityId, range))
    }

    private fun com.soyle.stories.domain.prose.content.ProseContent.isIntersecting(range: IntRange): Boolean
    {
        if (startIndex <= endIndex || range.isEmpty()) return false
        return startIndex in range || range.start in startIndex until endIndex
    }

    fun withTextInserted(
        text: String,
        index: Int? = null
    ): ProseUpdate<TextInsertedIntoProse> {
        val insertIndex = index ?: this.text.length
        if (mentions.any { it.isBisectedBy(insertIndex) }) {
            throw ProseMentionCannotBeBisected()
        }
        val (mentionsBeforeIndex, mentionsAfterIndex) = _mentions.partition { it.startIndex < insertIndex }
        val newText = StringBuilder(this.text).insert(insertIndex, text).toString()
        val newProse = copy(
            text = newText,
            mentions = mentionsBeforeIndex + mentionsAfterIndex.map { it.shiftedRight(text.length, newText) }
        )
        return newProse.updatedBy(TextInsertedIntoProse(newProse, text, insertIndex))
    }

    private fun com.soyle.stories.domain.prose.content.ProseContent.isBisectedBy(index: Int): Boolean {
        return index in startIndex + 1 until endIndex
    }

    fun withMentionTextReplaced(entityId: MentionedEntityId<*>, replacement: Pair<String, String>): ProseUpdate<MentionTextReplaced?> {
        val mentionsOfEntityWithMatch = mentionsByEntityId[entityId].orEmpty().filter {
            text.substring(it.startIndex, it.endIndex) == replacement.first
        }
        if (mentionsOfEntityWithMatch.isEmpty()) return this.updatedBy(null)

        val lengthDifference = replacement.second.length - replacement.first.length
        val contentBuilder = StringBuilder(text)
        mentionsOfEntityWithMatch.fold(0) { adjustment, mention ->
            contentBuilder.replace(mention.startIndex + adjustment, mention.endIndex + adjustment, replacement.second)
            adjustment + lengthDifference
        }
        val newText = contentBuilder.toString()

        var adjustment = 0
        val newMentions = sortedMentionSet.map { mention ->
            val shiftedMention = mention.shiftedRight(adjustment, newText)
            if (shiftedMention.entityId == entityId) {
                adjustment += lengthDifference
                shiftedMention.withLength(replacement.second.length, newText)
            } else {
                shiftedMention
            }
        }

        val newProse = copy(text = newText, mentions = newMentions)

        return newProse.updatedBy(
            MentionTextReplaced(
                newProse,
                entityId,
                text.substring(mentionsOfEntityWithMatch.first().startIndex, mentionsOfEntityWithMatch.first().endIndex),
                replacement.second
            )
        )

    }

    fun withTextRemoved(
        range: IntRange
    ): ProseUpdate<TextRemovedFromProse> {
        if (range.first < 0 || range.last > text.length)
            throw IndexOutOfBoundsException("Acceptable range to remove text is 0..${text.length}.  Received $range")
        if (_mentions.any { it.position.isBisectedBy(range.first) || it.position.isBisectedBy(range.last+1) }) {
            throw ProseMentionCannotBeBisected()
        }
        val rangeLength = (range.last + 1) - range.first
        val (mentionsBeforeRange, mentionsAfterRangeStart) = _mentions.partition { it.position.index < range.first }
        val (mentionsInRange, mentionsAfterRange) = mentionsAfterRangeStart.partition { it.end() < range.last }
        val newText = StringBuilder(text).removeRange(range).toString()

        val newProse = copy(
            text = newText,
            mentions = mentionsBeforeRange + mentionsAfterRange.map { it.shiftedLeft(rangeLength, newText) }
        )
        return newProse.updatedBy(TextRemovedFromProse(newProse, text.substring(range), range.first))
    }

    fun withoutMention(
        entityId: MentionedEntityId<*>,
        startIndex: Int
    ): ProseUpdate<MentionRemovedFromProse> {
        if (! containsMentionOf(entityId)) throw MentionDoesNotExistInProse(id, entityId, startIndex)
        val mention = mentionsByEntityId[entityId].orEmpty().find { it.startIndex == startIndex }
            ?: throw MentionDoesNotExistInProse(id, entityId, startIndex)

        val newProse = copy(
            mentions = _mentions.filterNot { it === mention }
        )
        return newProse.updatedBy(MentionRemovedFromProse(newProse, entityId, ProseMentionRange(startIndex, mention.endIndex- startIndex)))
    }

    fun withContentReplaced(content: List<ProseContent>): ProseUpdate<ContentReplaced> {
        val newText = content.joinToString("") { proseContent ->
            proseContent.text + (proseContent.mention?.second ?: "")
        }
        var offset = 0
        val newMentions = content.mapNotNull { (leadingText, mention) ->
            if (mention == null) return@mapNotNull null
            offset += leadingText.length
            ProseMention(mention.first, ProseMentionRange(offset, mention.second.length), newText).also {
                offset += mention.second.length
            }
        }
        val newProse = copy(
            text = newText,
            mentions = newMentions
        )
        return newProse.updatedBy(ContentReplaced(newProse))
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Prose($uuid)"
    }


    private class ProseText(
        override val text: String,
        override val startIndex: Int,
        override val endIndex: Int
    ) : com.soyle.stories.domain.prose.content.ProseContent.Text

    private class ProseMention<Id : Any>(
        override val entityId: MentionedEntityId<Id>,
        val position: ProseMentionRange,
        fullText: String
    ) : com.soyle.stories.domain.prose.content.ProseContent.Mention<Id> {
        fun start(): Int = position.index
        fun end(): Int = position.index + position.length

        override val startIndex: Int
            get() = position.index
        override val endIndex: Int
            get() = position.index + position.length
        override val text: SingleLine by lazy { countLines(fullText.substring(startIndex, endIndex)) as SingleLine }

        fun shiftedRight(amount: Int, fullText: String) = ProseMention(entityId = entityId, position = position.shiftedRight(amount), fullText)

        fun shiftedLeft(amount: Int, fullText: String) = ProseMention(entityId = entityId, position = position.shiftedLeft(amount), fullText)

        fun withLength(length: Int, fullText: String) = ProseMention(entityId = entityId, position = position.withLength(length), fullText)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ProseMention<*>

            if (entityId != other.entityId) return false
            if (position != other.position) return false

            return true
        }

        private val _hashCode by lazy { listOf(position).fold(entityId.hashCode()) { a, b -> 31 * a + b.hashCode()} }
        override fun hashCode(): Int = _hashCode

        override fun toString(): String {
            return "ProseMention(entityId=$entityId, position=$position)"
        }


    }

}

data class ProseContent(val text: String, val mention: Pair<MentionedEntityId<*>, SingleLine>?)

sealed class MentionedEntityId<Id : Any> {
    abstract val id: Id
}

data class MentionedCharacterId(override val id: Character.Id) : MentionedEntityId<Character.Id>()
data class MentionedLocationId(override val id: Location.Id) : MentionedEntityId<Location.Id>()
data class MentionedSymbolId(override val id: Symbol.Id, val themeId: Theme.Id) : MentionedEntityId<Symbol.Id>()

fun Character.Id.mentioned() = MentionedCharacterId(this)
fun Location.Id.mentioned() = MentionedLocationId(this)
fun Symbol.Id.mentioned(fromTheme: Theme.Id) = MentionedSymbolId(this, fromTheme)

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


fun <E : ProseEvent?> Prose.updatedBy(event: E) = ProseUpdate(this, event)

