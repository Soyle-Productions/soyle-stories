package com.soyle.stories.domain.prose.events

import com.soyle.stories.domain.prose.Prose

class TextRemovedFromProse(
    prose: Prose,
    val deletedText: String,
    val index: Int
) : ProseEvent(prose) {
    val newMentions = prose.mentions
}