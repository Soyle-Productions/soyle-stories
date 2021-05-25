package com.soyle.stories.prose.mentionTextReplaced

import com.soyle.stories.domain.prose.events.MentionTextReplaced

interface MentionTextReplacedReceiver {
    suspend fun receiveMentionTextReplaced(mentionTextReplaced: MentionTextReplaced)
}