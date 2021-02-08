package com.soyle.stories.prose.editProse

import com.soyle.stories.usecase.prose.updateProse.UpdateProse

class UpdateProseOutput(
    private val contentReplacedReceiver: ContentReplacedReceiver
) : UpdateProse.OutputPort {
    override suspend fun invoke(response: UpdateProse.ResponseModel) {
        contentReplacedReceiver.receiveContentReplacedEvent(response.contentReplaced)
    }
}