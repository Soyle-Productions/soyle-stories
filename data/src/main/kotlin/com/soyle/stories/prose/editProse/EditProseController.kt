package com.soyle.stories.prose.editProse

import com.soyle.stories.common.EntityId

interface EditProseController {

    fun insertText(text: String, index: Int)
    fun addMention(entityId: EntityId<*>, index: Int, length: Int)

}