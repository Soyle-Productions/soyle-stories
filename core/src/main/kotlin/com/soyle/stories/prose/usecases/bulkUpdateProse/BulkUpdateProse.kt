package com.soyle.stories.prose.usecases.bulkUpdateProse

import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.ProseEvent

interface BulkUpdateProse {

    suspend operator fun invoke(proseId: Prose.Id, revision: Long, operations: List<Operation>, output: OutputPort)

    class ResponseModel(
        val events: List<ProseEvent>
    )

    interface OutputPort {
        suspend fun receiveBulkUpdateResponse(response: ResponseModel)
    }

}