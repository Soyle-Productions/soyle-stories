package com.soyle.stories.prose.entityMentionedInProse

import com.soyle.stories.common.Notifier
import com.soyle.stories.prose.EntityMentionedInProse

class EntityMentionedInProseNotifier : Notifier<EntityMentionedInProseReceiver>(), EntityMentionedInProseReceiver {
    override suspend fun receiveEntityMentionedInProse(event: EntityMentionedInProse) {
        notifyAll { it.receiveEntityMentionedInProse(event) }
    }
}