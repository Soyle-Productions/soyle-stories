package com.soyle.stories.prose.usecases.detectInvalidMentions

import com.soyle.stories.entities.MentionedEntityId
import com.soyle.stories.entities.Prose

interface DetectInvalidatedMentions {
    suspend operator fun invoke(proseId: Prose.Id, output: OutputPort)

    class ResponseModel(
        val proseId: Prose.Id,
        val invalidEntityIds: Collection<MentionedEntityId<*>>
    )

    interface OutputPort
    {
        suspend fun receiveDetectedInvalidatedMentions(response: ResponseModel)
    }
}