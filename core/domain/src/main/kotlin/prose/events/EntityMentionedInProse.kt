package com.soyle.stories.domain.prose.events

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseMentionRange

class EntityMentionedInProse(
    prose: Prose,
    val entityId: MentionedEntityId<*>,
    val position: ProseMentionRange
) : ProseEvent(prose)