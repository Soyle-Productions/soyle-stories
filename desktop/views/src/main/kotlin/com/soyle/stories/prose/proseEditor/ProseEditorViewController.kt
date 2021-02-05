package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.NonBlankString
import kotlin.math.abs

class ProseEditorViewController(
    private val viewListener: ProseEditorViewListener,
    private val state: ProseEditorState,
    private val setCaretPosition: (Int) -> Unit,
    private val setSelection: (IntRange) -> Unit
) {

    fun onTextAreaFocusLost() = viewListener.save()

    fun onCaretMoved(caretPosition: Int?)
    {
        if (caretPosition == null) return
        val adjustedPosition = getAdjustedPositionIfBisectingMention(caretPosition)
        if (adjustedPosition != caretPosition) setCaretPosition(adjustedPosition)
    }

    fun onSelectionChanged(range: IntRange)
    {
        val adjustedStart = getAdjustedPositionIfBisectingMention(range.first)

        if (range.last == range.first) {
            if (adjustedStart != range.first) setSelection(
                adjustedStart .. adjustedStart
            )
            return
        }

        val adjustedEnd = getAdjustedPositionIfBisectingMention(range.last)

        if (adjustedStart != range.first || adjustedEnd != range.last) setSelection(
            adjustedStart .. adjustedEnd
        )
    }

    fun onKeyTyped(character: String, position: Int) {
        val mentionQueryState = state.mentionQueryState.value
        if (mentionQueryState is PrimedQuery) {
            val query: NonBlankString? = if (mentionQueryState is TriggeredQuery) {
                if (character == "\b") NonBlankString.create(mentionQueryState.query.dropLast(1))
                else NonBlankString.create(mentionQueryState.query + character)
            } else {
                if (character == "\b") null
                else NonBlankString.create(character)
            }
            if (query is NonBlankString) {
                viewListener.getStoryElementsContaining(query)
            } else {
                viewListener.cancelQuery()
            }
        } else {
            if (character == "@") {
                viewListener.primeMentionQuery(position)
            }
        }
    }




    private fun getAdjustedPositionIfBisectingMention(position: Int): Int {
        val bisectedMention = findSegment { mention, offset ->
            position > offset && position < (offset + mention.text.length)
        }

        return if (bisectedMention != null) {
            val bisectedMentionStart = bisectedMention.second
            val bisectedMentionEnd = bisectedMention.second + bisectedMention.first.text.length

            val startDistance = abs(bisectedMentionStart - position)
            val endDistance = abs(bisectedMentionEnd - position)
            if (startDistance < endDistance) bisectedMentionStart else bisectedMentionEnd
        } else position
    }

    private fun findSegment(predicate: (Mention, Int) -> Boolean): Pair<Mention, Int>? {
        var offset = 0
        return state.content
            .find {
                if (it !is Mention) {
                    offset += it.text.length
                    return@find false
                }
                val predicateResult = predicate(it, offset)
                if (predicateResult) true
                else {
                    offset += it.text.length
                    false
                }
            }?.let {
                it as Mention to offset
            }
    }
}