package com.soyle.stories.prose.entityMentionedInProse

import com.soyle.stories.prose.EntityMentionedInProse

interface EntityMentionedInProseReceiver {
    suspend fun receiveEntityMentionedInProse(event: EntityMentionedInProse)
}