package com.soyle.stories.prose.proseCreated

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.prose.events.ProseCreated

class ProseCreatedNotifier : Notifier<ProseCreatedReceiver>(), ProseCreatedReceiver {
    override suspend fun receiveProseCreated(event: ProseCreated) {
        notifyAll { it.receiveProseCreated(event) }
    }
}