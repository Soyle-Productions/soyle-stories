package com.soyle.stories.prose.editProse

import com.soyle.stories.domain.prose.ContentReplaced

interface ContentReplacedReceiver {

    suspend fun receiveContentReplacedEvent(contentReplaced: ContentReplaced)

}