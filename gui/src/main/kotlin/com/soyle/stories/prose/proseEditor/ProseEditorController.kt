package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.SingleLine
import com.soyle.stories.common.countLines
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseContent
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.entities.ProseMentionRange
import com.soyle.stories.gui.View
import com.soyle.stories.prose.ContentReplaced
import com.soyle.stories.prose.editProse.ContentReplacedReceiver
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.readProse.ReadProseController
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene

fun interface OnLoadMentionQueryOutput : (List<GetStoryElementsToMentionInScene.MatchingStoryElement>) -> Unit

class ProseEditorController private constructor(
    private val proseId: Prose.Id,
    private val view: View.Nullable<ProseEditorViewModel>,
    private val readProseController: ReadProseController,
    private val editProseController: EditProseController,
    private val onLoadMentionQuery: (NonBlankString, OnLoadMentionQueryOutput) -> Unit,
    private val presenter: ProseEditorPresenter
) : ProseEditorViewListener, ContentReplacedReceiver {

    constructor(
        proseId: Prose.Id,
        view: View.Nullable<ProseEditorViewModel>,
        readProseController: ReadProseController,
        editProseController: EditProseController,
        onLoadMentionQuery: (NonBlankString, OnLoadMentionQueryOutput) -> Unit
    ) : this(
        proseId,
        view,
        readProseController,
        editProseController,
        onLoadMentionQuery,
        ProseEditorPresenter(view)
    )

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

    override fun selectStoryElement(filteredListIndex: Int) {
        val queryState = view.viewModel?.mentionQueryState ?: return
        if (queryState is MentionQueryLoaded) {
            val match = queryState.prioritizedMatches[filteredListIndex]
            val newMention = ProseMention(match.id, ProseMentionRange(queryState.primedIndex, match.name.length))
            presenter.replaceRangeWithMention(
                queryState.primedIndex .. queryState.primedIndex + queryState.query.length + 1,
                newMention,
                match.name
            )
        }
    }
/*
    override fun delete(start: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun backspace(start: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteRange(start: Int, end: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun replaceRange(start: Int, end: Int, replacementText: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun insert(start: Int, text: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun moveCursorLeft(currentPosition: Int): Int {
        TODO("Not yet implemented")
    }

    override fun moveCursorRight(currentPosition: Int): Int {
        TODO("Not yet implemented")
    }

    override fun moveCursorTo(position: Int): Int {
        TODO("Not yet implemented")
    }*/

    override fun getStoryElementsContaining(query: NonBlankString) {
        val queryState = view.viewModel?.mentionQueryState ?: return
        when (queryState) {
            is MentionQueryPrimed -> {
                view.updateOrInvalidated {
                    copy(
                        mentionQueryState = MentionQueryLoading(
                            query.value,
                            query.value,
                            queryState.primedIndex
                        )
                    )
                }
                onLoadMentionQuery(query, presenter)
            }
            is MentionQueryLoading -> {
                view.updateOrInvalidated {
                    copy(
                        mentionQueryState = MentionQueryLoading(
                            queryState.initialQuery,
                            query.value,
                            queryState.primedIndex
                        )
                    )
                }
            }
            is MentionQueryLoaded -> {
                view.updateOrInvalidated { copy(mentionQueryState = with(presenter) { queryState.updateQuery(query.value) }) }
            }
            else -> return
        }
    }

    override suspend fun receiveContentReplacedEvent(contentReplaced: ContentReplaced) {
        if (contentReplaced.proseId != proseId) return
        presenter.receiveContentReplacedEvent(contentReplaced)
    }


    override fun save() {
        val viewModel = view.viewModel ?: return
        if (viewModel.isLocked) return
        val content = viewModel.content
        presenter.replaceContentAndLockInput(content)

        val paddedContent = if (content.firstOrNull() !is BasicText) listOf(BasicText("")) + content else content
        val proseContent = paddedContent.asSequence().windowed(2, 1, true) {
            val text = it.firstOrNull() as? BasicText ?: return@windowed null
            val mention = it.getOrNull(1) as? Mention
            ProseContent(text.text, mention?.let { it.entityId to countLines(it.text) as SingleLine })
        }.filterNotNull().toList()


        editProseController.updateProse(proseId, proseContent).invokeOnCompletion { failure ->
            if (failure != null) presenter.unlockInput()
        }
    }

}