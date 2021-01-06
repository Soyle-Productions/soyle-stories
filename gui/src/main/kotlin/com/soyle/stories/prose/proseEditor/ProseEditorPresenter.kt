package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.SingleLine
import com.soyle.stories.common.countLines
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.gui.View
import com.soyle.stories.prose.ContentReplaced
import com.soyle.stories.prose.editProse.ContentReplacedReceiver
import com.soyle.stories.prose.usecases.readProse.ReadProse
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene

class ProseEditorPresenter internal constructor(
    private val view: View.Nullable<ProseEditorViewModel>
) : ReadProse.OutputPort, OnLoadMentionQueryOutput, ContentReplacedReceiver {

    @Synchronized
    override suspend fun receiveProse(response: ReadProse.ResponseModel) {
        view.update {
            ProseEditorViewModel(
                versionNumber = response.revision,
                isLocked = false,
                breakBodyIntoContentElements(response.body, response.mentions),
                mentionQueryState = NoQuery
            )
        }
    }

    override fun invoke(matchingStoryElements: List<GetStoryElementsToMentionInScene.MatchingStoryElement>) {
        view.updateOrInvalidated {
            if (mentionQueryState !is MentionQueryLoading) return@updateOrInvalidated this
            copy(
                mentionQueryState = MentionQueryLoaded(
                    mentionQueryState.initialQuery,
                    mentionQueryState.query,
                    mentionQueryState.primedIndex,
                    matchingStoryElements,
                    listOf()
                ).updateQuery(mentionQueryState.query)
            )
        }
    }

    internal fun MentionQueryLoaded.updateQuery(query: String): MentionQueryState {
        val newMatches = matchesForInitialQuery
            .filter { it.name.contains(query, ignoreCase = true) }
            .sortedBy {
                val index = it.name.indexOf(query, ignoreCase = true)
                when {
                    index == 0 -> 0
                    it.name.substring(index - 1, index) == " " -> 1
                    else -> 2
                }
            }.map {
                MatchingStoryElementViewModel(
                    countLines(it.name) as SingleLine,
                    it.name.indexOf(query, ignoreCase = true).let { it until it + query.length },
                    when (it.entityId.id) {
                        is Character.Id -> "character"
                        is Location.Id -> "location"
                        else -> error("unrecognized entity type")
                    },
                    it.entityId
                )
            }
        if (newMatches.isEmpty()) return NoQuery
        return MentionQueryLoaded(
            initialQuery,
            query,
            primedIndex,
            matchesForInitialQuery,
            newMatches
        )
    }


    override suspend fun receiveContentReplacedEvent(contentReplaced: ContentReplaced) {
        view.updateOrInvalidated {
            copy(
                versionNumber = contentReplaced.revision,
                isLocked = false,
                content = breakBodyIntoContentElements(contentReplaced.newContent, contentReplaced.newMentions)
            )
        }
    }

    private fun breakBodyIntoContentElements(body: String, mentions: List<ProseMention<*>>): List<ContentElement> {
        var lastMentionEnd = 0
        return mentions.flatMap { mention ->
            listOf(
                BasicText(body.substring(lastMentionEnd, mention.start())),
                Mention(body.substring(mention.start(), mention.end()), mention.entityId)
            ).also {
                lastMentionEnd = mention.end()
            }
        } + listOfNotNull(
            if (lastMentionEnd < body.length) BasicText(body.substring(lastMentionEnd, body.length)) else null
        )
    }

    internal fun replaceContentAndLockInput(content: List<ContentElement>) {
        view.updateOrInvalidated {
            copy(
                isLocked = true,
                content = content
            )
        }
    }

    internal fun unlockInput() {
        view.updateOrInvalidated {
            copy(
                isLocked = false
            )
        }
    }

    internal fun replaceRangeWithMention(range: IntRange, mention: ProseMention<*>, mentionedText: SingleLine) {
        val newElement = Mention(mentionedText.toString(), mention.entityId)
        view.updateOrInvalidated {
            copy(
                content = listOf(newElement)//content.substring(0 until range.first) + mentionedText + content.substring(range.last)
            )
        }
    }

}