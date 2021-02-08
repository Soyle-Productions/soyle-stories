package com.soyle.stories.prose.textInsertedIntoProse

import com.soyle.stories.domain.prose.TextInsertedIntoProse

interface TextInsertedIntoProseReceiver {
    suspend fun receiveTextInsertedIntoProse(event: TextInsertedIntoProse)
}