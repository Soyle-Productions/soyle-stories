package com.soyle.stories.prose.usecases.readProse

import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseMention

interface ReadProse {

    suspend operator fun invoke(proseId: Prose.Id, output: OutputPort)

    class ResponseModel(
        val proseId: Prose.Id,
        val revision: Long,
        val body: String,
        val mentions: List<ProseMention<*>>
    )

    interface OutputPort {
        suspend fun receiveProse(response: ResponseModel)
    }

}