package com.soyle.stories.domain.prose.content

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.validation.SingleLine

interface ProseContent {

    val text: CharSequence

    val startIndex: Int
    val endIndex: Int

    interface Text : ProseContent {

        override val text: String
    }
    interface Mention<Id : Any> : ProseContent {

        override val text: SingleLine

        val entityId: MentionedEntityId<Id>

    }

}