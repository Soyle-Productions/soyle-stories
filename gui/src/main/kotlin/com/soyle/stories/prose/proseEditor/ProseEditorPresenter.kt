package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.SingleLine
import com.soyle.stories.common.countLines
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.gui.View
import com.soyle.stories.prose.ContentReplaced
import com.soyle.stories.prose.MentionTextReplaced
import com.soyle.stories.prose.editProse.ContentReplacedReceiver
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver
import com.soyle.stories.prose.usecases.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.prose.usecases.readProse.ReadProse
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene
import com.soyle.stories.scene.usecases.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse

class ProseEditorPresenter internal constructor(
    private val view: View.Nullable<ProseEditorViewModel>
) : ReadProse.OutputPort, OnLoadMentionQueryOutput, ContentReplacedReceiver, MentionTextReplacedReceiver,
    DetectInvalidatedMentions.OutputPort, OnLoadMentionReplacementsOutput {

    @Synchronized
    override suspend fun receiveProse(response: ReadProse.ResponseModel) {
        view.update {
            ProseEditorViewModel(
                versionNumber = response.revision,
                isLocked = false,
                breakBodyIntoContentElements(response.body, response.mentions),
                mentionQueryState = NoQuery,
                replacementOptions = null
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

    override suspend fun receiveMentionTextReplaced(mentionTextReplaced: MentionTextReplaced) {
        view.updateOrInvalidated {
            copy(
                versionNumber = mentionTextReplaced.revision,
                isLocked = false,
                content = breakBodyIntoContentElements(mentionTextReplaced.newContent, mentionTextReplaced.newMentions)
            )
        }
    }

    override suspend fun receiveDetectedInvalidatedMentions(response: DetectInvalidatedMentions.ResponseModel) {
        val removedEntityIds = response.invalidEntityIds.toSet()
        view.updateOrInvalidated {
            copy(
                content = content.map {
                    if (it is Mention) {
                        if (it.entityId in removedEntityIds) it.copy(issue = "Removed")
                        else it.copy(issue = null)
                    } else it
                }
            )
        }
    }

    override fun loadedReplacements(replacements: List<ListOptionsToReplaceMentionInSceneProse.MentionOption<*>>) {
        view.updateOrInvalidated {
            copy(
                replacementOptions = replacements.map {
                    ReplacementElementViewModel(it.name, it.entityId)
                }
            )
        }
    }

    private fun breakBodyIntoContentElements(body: String, mentions: List<ProseMention<*>>): List<ContentElement> {
        var lastMentionEnd = 0
        return mentions.flatMap { mention ->
            val mentionText = body.substring(mention.start(), mention.end())
            body.splitIntoBasicTextLines(lastMentionEnd, mention.start())
                .plus(Mention(mentionText, mention.entityId))
                .also { lastMentionEnd = mention.end() }
        } + if (lastMentionEnd < body.length) body.splitIntoBasicTextLines(lastMentionEnd, body.length) else listOf()
    }

    private fun String.splitIntoBasicTextLines(start: Int, end: Int) = listOf(substring(start, end))
        .map(::BasicText)

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
            var offset = 0
            copy(
                content = content.flatMap { element ->
                    if (range.intersect(offset..offset + element.text.length).isNotEmpty()) {
                        listOfNotNull(
                            BasicText(element.text.substring(0 until range.first - offset)).takeUnless { it.text.isEmpty() },
                            newElement,
                            BasicText(element.text.substring(range.last - offset)).takeUnless { it.text.isEmpty() }
                        )
                    } else {
                        offset += element.text.length
                        listOf(element)
                    }
                }
            )
        }
    }

}