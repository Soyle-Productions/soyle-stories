package com.soyle.stories.prose.entityMentionedInProse

import com.soyle.stories.domain.prose.events.EntityMentionedInProse

interface EntityMentionedInProseReceiver {
    suspend fun receiveEntityMentionedInProse(event: EntityMentionedInProse)
}