package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.SingleLine
import com.soyle.stories.entities.MentionedEntityId
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene

data class ProseEditorViewModel(
    val versionNumber: Long,
    val isLocked: Boolean,
    val content: List<ContentElement>,
    val mentionQueryState: MentionQueryState,
    val replacementOptions: List<ReplacementElementViewModel>?
)

sealed class ContentElement {
    abstract val text: String
}
data class BasicText(override val text: String) : ContentElement()
data class Mention(override val text: String, val entityId: MentionedEntityId<*>, val issue: String? = null) : ContentElement()

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

data class MatchingStoryElementViewModel(val name: SingleLine, val addendum: SingleLine?, val matchingRange: IntRange, val type: String, val id: MentionedEntityId<*>)

data class ReplacementElementViewModel(val name: String, val id: MentionedEntityId<*>)