package com.soyle.stories.prose.editProse

import com.soyle.stories.prose.ContentReplaced

interface ContentReplacedReceiver {

    suspend fun receiveContentReplacedEvent(contentReplaced: ContentReplaced)

}