package com.soyle.stories.prose.proseEditor

import com.soyle.stories.domain.prose.content.ProseContent
import com.soyle.stories.domain.prose.events.ContentReplaced
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.gui.View
import com.soyle.stories.prose.editProse.ContentReplacedReceiver
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver
import com.soyle.stories.usecase.prose.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.usecase.prose.readProse.ReadProse
import com.soyle.stories.usecase.scene.prose.mentions.AvailableStoryElementsToMentionInScene

class ProseEditorPresenter internal constructor(
    private val view: View.Nullable<ProseEditorViewModel>
) : ReadProse.OutputPort, OnLoadMentionQueryOutput, ContentReplacedReceiver, MentionTextReplacedReceiver,
    DetectInvalidatedMentions.OutputPort, OnLoadMentionReplacementsOutput {

    override suspend fun receiveProse(response: ReadProse.ResponseModel) {
        view.update {
            ProseEditorViewModel(
                versionNumber = response.revision,
                isLocked = false,
                breakBodyIntoContentElements(response.body, response.mentions),
                mentionQueryState = null,
                replacementOptions = null
            )
        }
    }

//    override fun invoke(matchingStoryElements: List<GetStoryElementsToMentionInScene.MatchingStoryElement>) {
//        view.updateOrInvalidated {
//            if (mentionQueryState !is MentionQueryLoading) return@updateOrInvalidated this
//            copy(
//                mentionQueryState = MentionQueryLoaded(
//                    mentionQueryState.initialQuery,
//                    mentionQueryState.query,
//                    mentionQueryState.primedIndex,
//                    matchingStoryElements,
//                    listOf()
//                ).updateQuery(mentionQueryState.query)
//            )
//        }
//    }

//    internal fun MentionQueryLoaded.updateQuery(query: String): MentionQueryState {
//        val mentionedIds = view.viewModel!!.content.asSequence().filterIsInstance<Mention>().map { it.entityId }.toSet()
//        val newMatches = matchesForInitialQuery
//            .filter { it.name.contains(query, ignoreCase = true) }
//            .sortedWith(compareBy(
//                {
//                    it.entityId !in mentionedIds
//                },
//                {
//                    val index = it.name.indexOf(query, ignoreCase = true)
//                    when {
//                        index == 0 -> 0
//                        it.name.substring(index - 1, index) == " " -> 1
//                        else -> 2
//                    }
//                },
//                {
//                    it.name.length.toDouble() / query.length
//                }
//            )).map {
//                MatchingStoryElementViewModel(
//                    countLines(it.name) as SingleLine,
//                    it.parentEntityName?.let { countLines(it) as? SingleLine },
//                    it.name.indexOf(query, ignoreCase = true).let { it until it + query.length },
//                    when (it.entityId) {
//                        is MentionedCharacterId -> "character"
//                        is MentionedLocationId -> "location"
//                        is MentionedSymbolId -> "symbol"
//                    },
//                    it.entityId
//                )
//            }
//        if (newMatches.isEmpty()) return NoQuery
//        return MentionQueryLoaded(
//            initialQuery,
//            query,
//            primedIndex,
//            matchesForInitialQuery,
//            newMatches
//        )
//    }


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
//
//    override fun loadedReplacements(replacements: List<ListOptionsToReplaceMentionInSceneProse.MentionOption<*>>) {
//        view.updateOrInvalidated {
//            val mentionedEntities = content.asSequence().filterIsInstance<Mention>().map { it.entityId }.toSet()
//            copy(
//                replacementOptions = replacements.sortedBy { it.entityId !in mentionedEntities }.map {
//                    ReplacementElementViewModel(it.name, it.entityId)
//                }
//            )
//        }
//    }

    private fun breakBodyIntoContentElements(body: String, mentions: List<ProseContent.Mention<*>>): List<ContentElement> {
        var lastMentionEnd = 0
        return mentions.flatMap { mention ->
            val mentionText = body.substring(mention.startIndex, mention.endIndex)
            body.splitIntoBasicTextLines(lastMentionEnd, mention.startIndex)
                .plus(Mention(mentionText, mention.entityId))
                .also { lastMentionEnd = mention.endIndex }
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

    internal fun replaceRangeWithMention(range: IntRange, mention: ProseContent.Mention<*>, mentionedText: SingleLine) {
        val newElement = Mention(mentionedText.toString(), mention.entityId)
        view.updateOrInvalidated {
            var offset = 0
            copy(
                content = content.flatMap { element ->
                    if (range.intersect(offset until offset + element.text.length).isNotEmpty()) {
                        try {
                            listOfNotNull(
                                BasicText(element.text.substring(0 until range.first - offset)).takeUnless { it.text.isEmpty() },
                                newElement,
                                BasicText(element.text.substring(range.last - offset)).takeUnless { it.text.isEmpty() }
                            ).also {
                                offset += newElement.text.length
                            }
                        } catch (t: StringIndexOutOfBoundsException) {
                            throw IllegalStateException(
                                """Substring failed:
                                     range: $range
                                     mention: $mention
                                     offset: $offset
                                     element: $element
                            """.trimIndent(), t)
                        }
                    } else {
                        offset += element.text.length
                        listOf(element)
                    }
                }
            )
        }
    }

    override fun loadedReplacements(replacements: AvailableStoryElementsToMentionInScene) {
    }

    override fun invoke(p1: AvailableStoryElementsToMentionInScene) {
    }

}