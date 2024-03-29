package com.soyle.stories.prose.mentionTextReplaced

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.prose.events.MentionTextReplaced

class MentionTextReplacedNotifier : Notifier<MentionTextReplacedReceiver>(), MentionTextReplacedReceiver {
    override suspend fun receiveMentionTextReplaced(mentionTextReplaced: MentionTextReplaced) {
        notifyAll { it.receiveMentionTextReplaced(mentionTextReplaced) }
    }
}