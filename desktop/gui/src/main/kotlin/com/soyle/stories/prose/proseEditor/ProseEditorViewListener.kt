package com.soyle.stories.prose.proseEditor

import com.soyle.stories.domain.validation.NonBlankString

interface ProseEditorViewListener {
    fun getValidState()
    fun primeMentionQuery(primedIndex: Int)
    fun getStoryElementsContaining(query: NonBlankString)
    fun cancelQuery()
    fun selectStoryElement(filteredListIndex: Int, andUseElement: Boolean)
    fun save()
    fun getMentionReplacementOptions(mention: Mention)
}