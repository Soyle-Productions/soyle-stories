package com.soyle.stories.domain.prose.events

import com.soyle.stories.domain.prose.Prose

class ContentReplaced(
    prose: Prose
) : ProseEvent(prose)
{
    val newContent = prose.text
    val newMentions = prose.mentions
}