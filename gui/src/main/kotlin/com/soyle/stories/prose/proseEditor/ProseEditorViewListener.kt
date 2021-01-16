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
    fun clearAllMentionsOfEntity(entityId: MentionedEntityId<*>)
}