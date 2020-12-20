package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.EntityId
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene

data class ProseEditorViewModel(
    val versionNumber: Long,
    val content: String,
    val mentions: List<ProseMention<*>>,
    val mentionQueryState: MentionQueryState
)

sealed class MentionQueryState
interface PrimedQuery {
    val primedIndex: Int
}
interface TriggeredQuery : PrimedQuery {
    val query: String
}
object NoQuery : MentionQueryState()
class MentionQueryPrimed(override val primedIndex: Int) : MentionQueryState(), PrimedQuery
class MentionQueryLoading(internal val initialQuery: String, override val query: String,
                          override val primedIndex: Int) : MentionQueryState(), TriggeredQuery
class MentionQueryLoaded(
    internal val initialQuery: String,
    override val query: String,
    override val primedIndex: Int,
    internal val matchesForInitialQuery: List<GetStoryElementsToMentionInScene.MatchingStoryElement>,
    val prioritizedMatches: List<MatchingStoryElementViewModel>
) : MentionQueryState(), TriggeredQuery

class MatchingStoryElementViewModel(val name: String, val matchingRange: IntRange, val type: String, val id: EntityId<*>)