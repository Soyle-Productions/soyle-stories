package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.MentionedEntityId

interface ProseEditorViewListener {
    fun getValidState()
    fun primeMentionQuery(primedIndex: Int)
    fun getStoryElementsContaining(query: NonBlankString)
    fun cancelQuery()
    fun selectStoryElement(filteredListIndex: Int, andUseElement: Boolean)
    fun save()
    fun clearMention(mention: Mention)
    fun clearAllMentionsOfEntity(entityId: MentionedEntityId<*>)
    fun removeMention(mention: Mention)
    fun removeAllMentionsOfEntity(entityId: MentionedEntityId<*>)
    fun getMentionReplacementOptions(mention: Mention)
    fun replaceMention(mention: Mention, element: ReplacementElementViewModel)
    fun replaceAllMentionsOfEntity(entityId: MentionedEntityId<*>, element: ReplacementElementViewModel)
}