package com.soyle.stories.prose.textInsertedIntoProse

import com.soyle.stories.prose.TextInsertedIntoProse

interface TextInsertedIntoProseReceiver {
    suspend fun receiveTextInsertedIntoProse(event: TextInsertedIntoProse)
}