package com.soyle.stories.prose.invalidateRemovedMentions

import com.soyle.stories.domain.prose.Prose

interface InvalidateRemovedMentionsController {
    fun invalidateRemovedMentions(prose: Prose.Id)
}