package com.soyle.stories.prose.editProse

import com.soyle.stories.prose.usecases.updateProse.UpdateProse

class UpdateProseOutput(
    private val contentReplacedReceiver: ContentReplacedReceiver
) : UpdateProse.OutputPort {
    override suspend fun invoke(response: UpdateProse.ResponseModel) {
        contentReplacedReceiver.receiveContentReplacedEvent(response.contentReplaced)
    }
}