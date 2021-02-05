package com.soyle.stories.prose.invalidateRemovedMentions

import com.soyle.stories.common.Notifier
import com.soyle.stories.prose.usecases.detectInvalidMentions.DetectInvalidatedMentions

class DetectInvalidatedMentionsOutput : Notifier<DetectInvalidatedMentions.OutputPort>(), DetectInvalidatedMentions.OutputPort {

    override suspend fun receiveDetectedInvalidatedMentions(response: DetectInvalidatedMentions.ResponseModel) {
        notifyAll { it.receiveDetectedInvalidatedMentions(response) }
    }

}