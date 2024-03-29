package com.soyle.stories.prose.proseCreated

import com.soyle.stories.domain.prose.events.ProseCreated

interface ProseCreatedReceiver {
    suspend fun receiveProseCreated(event: ProseCreated)
}