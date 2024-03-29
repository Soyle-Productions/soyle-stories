package com.soyle.stories.prose.proseEditor

import com.soyle.stories.character.nameVariant.remove.CharacterNameVariantRemovedReceiver
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.domain.character.events.CharacterNameVariantRemoved
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.prose.content.ProseContent
import com.soyle.stories.domain.prose.events.ContentReplaced
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.domain.validation.countLines
import com.soyle.stories.gui.View
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.usecase.location.deleteLocation.DeletedLocation
import com.soyle.stories.prose.editProse.ContentReplacedReceiver
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.invalidateRemovedMentions.InvalidateRemovedMentionsController
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver
import com.soyle.stories.prose.readProse.ReadProseController
import com.soyle.stories.usecase.prose.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.usecase.prose.readProse.ReadProse
import com.soyle.stories.usecase.scene.getStoryElementsToMention.GetStoryElementsToMentionInScene
import com.soyle.stories.usecase.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse
import com.soyle.stories.theme.deleteTheme.ThemeDeletedReceiver
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeReceiver
import com.soyle.stories.usecase.theme.deleteTheme.DeletedTheme
import com.soyle.stories.usecase.theme.removeSymbolFromTheme.SymbolRemovedFromTheme

fun interface OnLoadMentionQueryOutput : (List<GetStoryElementsToMentionInScene.MatchingStoryElement>) -> Unit
fun interface OnLoadMentionReplacementsOutput {

    fun loadedReplacements(replacements: List<ListOptionsToReplaceMentionInSceneProse.MentionOption<*>>)
}

class ProseEditorController private constructor(
    private val proseId: Prose.Id,
    private val view: View.Nullable<ProseEditorViewModel>,
    private val readProseController: ReadProseController,
    private val invalidateRemovedMentionsController: InvalidateRemovedMentionsController,
    private val editProseController: EditProseController,
    private val onLoadMentionQuery: (NonBlankString, OnLoadMentionQueryOutput) -> Unit,
    private val onUseStoryElement: (com.soyle.stories.domain.prose.content.ProseContent.Mention<*>) -> Unit,
    private val onLoadMentionReplacements: (MentionedEntityId<*>, OnLoadMentionReplacementsOutput) -> Unit,
    private val presenter: ProseEditorPresenter
) : ReadProse.OutputPort, ProseEditorViewListener, ContentReplacedReceiver, MentionTextReplacedReceiver,
    DetectInvalidatedMentions.OutputPort, RemovedCharacterReceiver, DeletedLocationReceiver, ThemeDeletedReceiver,
    SymbolRemovedFromThemeReceiver, CharacterNameVariantRemovedReceiver {

    constructor(
        proseId: Prose.Id,
        view: View.Nullable<ProseEditorViewModel>,
        readProseController: ReadProseController,
        invalidateRemovedMentionsController: InvalidateRemovedMentionsController,
        editProseController: EditProseController,
        onLoadMentionQuery: (NonBlankString, OnLoadMentionQueryOutput) -> Unit,
        onUseStoryElement: (com.soyle.stories.domain.prose.content.ProseContent.Mention<*>) -> Unit,
        onLoadMentionReplacements: (MentionedEntityId<*>, OnLoadMentionReplacementsOutput) -> Unit,
    ) : this(
        proseId,
        view,
        readProseController,
        invalidateRemovedMentionsController,
        editProseController,
        onLoadMentionQuery,
        onUseStoryElement,
        onLoadMentionReplacements,
        ProseEditorPresenter(view)
    )

    override fun getValidState() {
        readProseController.readProse(proseId, this)
    }

    override suspend fun receiveProse(response: ReadProse.ResponseModel) {
        presenter.receiveProse(response)
        invalidateRemovedMentionsController.invalidateRemovedMentions(proseId)
    }

    override fun getMentionReplacementOptions(mention: Mention) {
        onLoadMentionReplacements.invoke(mention.entityId, presenter)
    }

    override fun primeMentionQuery(primedIndex: Int) {
        val queryState = view.viewModel?.mentionQueryState ?: return
        if (queryState == NoQuery) view.updateOrInvalidated { copy(mentionQueryState = MentionQueryPrimed(primedIndex)) }
    }

    override fun cancelQuery() {
        val queryState = view.viewModel?.mentionQueryState ?: return
        if (queryState != NoQuery) view.updateOrInvalidated { copy(mentionQueryState = NoQuery) }
    }

    override fun selectStoryElement(filteredListIndex: Int, andUseElement: Boolean) {
        val queryState = view.viewModel?.mentionQueryState ?: return
        if (queryState is MentionQueryLoaded) {
            val match = queryState.prioritizedMatches[filteredListIndex]
            val newMention = object : ProseContent.Mention<Any> {
                override val entityId: MentionedEntityId<Any> = match.id as MentionedEntityId<Any>
                override val startIndex: Int = queryState.primedIndex
                override val endIndex: Int = queryState.primedIndex + match.name.length
                override val text: SingleLine = match.name
            }
            presenter.replaceRangeWithMention(
                queryState.primedIndex..queryState.primedIndex + queryState.query.length + 1,
                newMention,
                match.name
            )
            if (andUseElement) onUseStoryElement(newMention)
        }
    }

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
        invalidateRemovedMentionsController.invalidateRemovedMentions(proseId)
    }

    override suspend fun receiveMentionTextReplaced(mentionTextReplaced: MentionTextReplaced) {
        if (mentionTextReplaced.proseId != proseId) return
        presenter.receiveMentionTextReplaced(mentionTextReplaced)
        invalidateRemovedMentionsController.invalidateRemovedMentions(proseId)
    }

    override suspend fun receiveDetectedInvalidatedMentions(response: DetectInvalidatedMentions.ResponseModel) {
        if (response.proseId != proseId) return
        presenter.receiveDetectedInvalidatedMentions(response)
    }

    override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
        invalidateRemovedMentionsController.invalidateRemovedMentions(proseId)
    }

    override suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation) {
        invalidateRemovedMentionsController.invalidateRemovedMentions(proseId)
    }

    override suspend fun receiveDeletedTheme(deletedTheme: DeletedTheme) {
        invalidateRemovedMentionsController.invalidateRemovedMentions(proseId)
    }

    override suspend fun receiveSymbolRemovedFromTheme(symbolRemovedFromTheme: SymbolRemovedFromTheme) {
        invalidateRemovedMentionsController.invalidateRemovedMentions(proseId)
    }

    override suspend fun receiveCharacterNameVariantRemoved(event: CharacterNameVariantRemoved) {
        invalidateRemovedMentionsController.invalidateRemovedMentions(proseId)
    }

    override fun save() {
        val viewModel = view.viewModel ?: return
        if (viewModel.isLocked) return
        val content = viewModel.content
        updateProse(content)
    }

    private fun List<ContentElement>.collapseAdjacentBasicTexts(): List<ContentElement> {
        return fold(ArrayList<ContentElement>(this.size)) { collapsedList, element ->
            val previousElement = collapsedList.lastOrNull()
            if (element is BasicText && previousElement is BasicText) {
                collapsedList.removeLast()
                collapsedList.add(BasicText(previousElement.text + element.text))
            } else {
                collapsedList.add(element)
            }
            collapsedList
        }
    }

    private fun updateProse(content: List<ContentElement>) {
        presenter.replaceContentAndLockInput(content)
        val proseContent =
            content.fold(mutableListOf<com.soyle.stories.domain.prose.ProseContent>()) { resultList, element ->
                val previousElement = resultList.lastOrNull()
                val previousMention = previousElement?.mention
                when (element) {
                    is BasicText -> when {
                        previousMention == null && previousElement != null -> {
                            resultList.removeLast()
                            resultList.add(ProseContent(previousElement.text + element.text, null))
                        }
                        else -> {
                            resultList.add(ProseContent(element.text, null))
                        }
                    }
                    is Mention -> if (previousElement != null && previousMention == null) {
                        resultList.removeLast()
                        resultList.add(
                            ProseContent(
                                previousElement.text,
                                element.entityId to countLines(element.text) as SingleLine
                            )
                        )
                    } else {
                        resultList.add(ProseContent("", element.entityId to countLines(element.text) as SingleLine))
                    }
                }
                resultList
            }.toList()

        editProseController.updateProse(proseId, proseContent).invokeOnCompletion { failure ->
            if (failure != null) presenter.unlockInput()
        }
    }

}