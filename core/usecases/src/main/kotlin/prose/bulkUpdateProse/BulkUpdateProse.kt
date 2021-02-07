package com.soyle.stories.usecase.prose.bulkUpdateProse

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseEvent

interface BulkUpdateProse {

    suspend operator fun invoke(proseId: Prose.Id, revision: Long, operations: List<Operation>, output: OutputPort)

    class ResponseModel(
        val events: List<ProseEvent>
    )

    interface OutputPort {
        suspend fun receiveBulkUpdateResponse(response: ResponseModel)
    }

}