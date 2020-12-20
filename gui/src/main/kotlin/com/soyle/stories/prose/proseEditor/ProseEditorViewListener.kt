package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.NonBlankString

interface ProseEditorViewListener {
    fun getValidState()
    fun primeMentionQuery(primedIndex: Int)
    fun getStoryElementsContaining(query: NonBlankString)
    fun cancelQuery()
}