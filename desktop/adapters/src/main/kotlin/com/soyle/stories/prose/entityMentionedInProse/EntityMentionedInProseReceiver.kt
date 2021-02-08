package com.soyle.stories.prose.entityMentionedInProse

import com.soyle.stories.domain.prose.EntityMentionedInProse

interface EntityMentionedInProseReceiver {
    suspend fun receiveEntityMentionedInProse(event: EntityMentionedInProse)
}