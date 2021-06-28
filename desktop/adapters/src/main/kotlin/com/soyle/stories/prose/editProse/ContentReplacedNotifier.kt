package com.soyle.stories.prose.editProse

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.prose.events.ContentReplaced

class ContentReplacedNotifier : Notifier<ContentReplacedReceiver>(), ContentReplacedReceiver {

    override suspend fun receiveContentReplacedEvent(contentReplaced: ContentReplaced) {
        notifyAll { it.receiveContentReplacedEvent(contentReplaced) }
    }
}