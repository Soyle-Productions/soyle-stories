package com.soyle.stories.prose.textInsertedIntoProse

import com.soyle.stories.common.Notifier
import com.soyle.stories.prose.TextInsertedIntoProse

class TextInsertedIntoProseNotifier : Notifier<TextInsertedIntoProseReceiver>(), TextInsertedIntoProseReceiver {
    override suspend fun receiveTextInsertedIntoProse(event: TextInsertedIntoProse) {
        notifyAll { it.receiveTextInsertedIntoProse(event) }
    }
}