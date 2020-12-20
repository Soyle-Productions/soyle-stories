package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Prose
import com.soyle.stories.gui.View
import com.soyle.stories.prose.readProse.ReadProseController
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene

fun interface OnLoadMentionQueryOutput : (List<GetStoryElementsToMentionInScene.MatchingStoryElement>) -> Unit

class ProseEditorController(
    private val proseId: Prose.Id,
    private val view: View.Nullable<ProseEditorViewModel>,
    private val readProseController: ReadProseController,
    private val onLoadMentionQuery: (NonBlankString, OnLoadMentionQueryOutput) -> Unit
) : ProseEditorViewListener {

    private val presenter: ProseEditorPresenter = ProseEditorPresenter(view)

    override fun getValidState() {
        readProseController.readProse(proseId, presenter)
    }

    override fun primeMentionQuery(primedIndex: Int) {
        val queryState = view.viewModel?.mentionQueryState ?: return
        if (queryState == NoQuery) view.updateOrInvalidated { copy(mentionQueryState = MentionQueryPrimed(primedIndex)) }
    }

    override fun cancelQuery() {
        val queryState = view.viewModel?.mentionQueryState ?: return
        if (queryState != NoQuery) view.updateOrInvalidated { copy(mentionQueryState = NoQuery) }
    }

    override fun getStoryElementsContaining(query: NonBlankString) {
        val queryState = view.viewModel?.mentionQueryState ?: return
        when (queryState) {
            is MentionQueryPrimed -> {
                view.updateOrInvalidated { copy(mentionQueryState = MentionQueryLoading(query.value, query.value, queryState.primedIndex)) }
                onLoadMentionQuery(query, presenter::receiveLoadedMatchingStoryElements)
            }
            is MentionQueryLoading -> {
                view.updateOrInvalidated { copy(mentionQueryState = MentionQueryLoading(queryState.initialQuery, query.value, queryState.primedIndex)) }
            }
            is MentionQueryLoaded -> {
                view.updateOrInvalidated { copy(mentionQueryState = with(presenter) { queryState.updateQuery(query.value) }) }
            }
            else -> return
        }
    }

}