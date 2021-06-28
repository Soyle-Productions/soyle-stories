package com.soyle.stories.usecase.prose.detectInvalidMentions

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.Prose

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