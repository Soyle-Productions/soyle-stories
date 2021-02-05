package com.soyle.stories.prose.proseCreated

import com.soyle.stories.prose.ProseCreated

interface ProseCreatedReceiver {
    suspend fun receiveProseCreated(event: ProseCreated)
}