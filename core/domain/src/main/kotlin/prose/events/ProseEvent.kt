package com.soyle.stories.domain.prose.events

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseMentionRange

sealed class ProseEvent(prose: Prose) {
    val proseId: Prose.Id = prose.id
    val revision: Long = prose.revision
}
