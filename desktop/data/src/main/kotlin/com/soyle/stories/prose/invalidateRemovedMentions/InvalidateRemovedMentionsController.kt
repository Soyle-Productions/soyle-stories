package com.soyle.stories.prose.invalidateRemovedMentions

import com.soyle.stories.entities.Prose

interface InvalidateRemovedMentionsController {
    fun invalidateRemovedMentions(prose: Prose.Id)
}