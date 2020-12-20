package com.soyle.stories.prose.proseEditor

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.gui.View
import com.soyle.stories.prose.usecases.readProse.ReadProse
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene

class ProseEditorPresenter(
    private val view: View.Nullable<ProseEditorViewModel>
) : ReadProse.OutputPort {

    override suspend fun receiveProse(response: ReadProse.ResponseModel) {
        view.update {
            ProseEditorViewModel(
                versionNumber = response.revision,
                content = response.body,
                mentions = response.mentions,
                mentionQueryState = NoQuery
            )
        }
    }

    internal fun receiveLoadedMatchingStoryElements(matchingStoryElements: List<GetStoryElementsToMentionInScene.MatchingStoryElement>) {
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
            .groupBy {
                val index = it.name.indexOf(query, ignoreCase = true)
                when {
                    index == 0 -> "start"
                    it.name.substring(index - 1, index) == " " -> "word start"
                    else -> "midword"
                }
            }.let {
                it.getOrElse("start") { listOf() } +
                        it.getOrElse("word start") { listOf() } +
                        it.getOrElse("midword") { listOf() }
            }.map {
                MatchingStoryElementViewModel(
                    it.name,
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

}