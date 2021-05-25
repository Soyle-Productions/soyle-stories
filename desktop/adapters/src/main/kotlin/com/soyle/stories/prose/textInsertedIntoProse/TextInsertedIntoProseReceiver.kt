package com.soyle.stories.prose.textInsertedIntoProse

import com.soyle.stories.domain.prose.events.TextInsertedIntoProse

interface TextInsertedIntoProseReceiver {
    suspend fun receiveTextInsertedIntoProse(event: TextInsertedIntoProse)
}