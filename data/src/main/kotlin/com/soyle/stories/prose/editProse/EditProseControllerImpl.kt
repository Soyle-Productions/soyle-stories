package com.soyle.stories.prose.editProse

import com.soyle.stories.common.EntityId
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.usecases.bulkUpdateProse.BulkUpdateProse
import com.soyle.stories.prose.usecases.bulkUpdateProse.InsertText
import com.soyle.stories.prose.usecases.bulkUpdateProse.MentionEntity
import com.soyle.stories.prose.usecases.bulkUpdateProse.Operation

class EditProseControllerImpl(
    private val proseId: Prose.Id,
    startingRevisionNumber: Long,
    private val threadTransformer: ThreadTransformer,
    private val updateProse: BulkUpdateProse,
    private val updateProseOutput: BulkUpdateProse.OutputPort
) : EditProseController {

    override fun insertText(text: String, index: Int) {
        val operation = InsertText(text, index)
        addOperation(operation)
    }

    override fun addMention(entityId: EntityId<*>, index: Int, length: Int) {
        val operation = MentionEntity(entityId, index, length)
        addOperation(operation)
    }

    @Synchronized
    private fun addOperation(operation: Operation)
    {
        nextUpdate.second.add(operation)
        if (! processingOperations) {
            sendBatch(nextUpdate.first, nextUpdate.second)
        }
    }

    @Synchronized
    private fun sendBatch(revision: Long, operations: List<Operation>) {
        processingOperations = true
        nextUpdate = nextUpdate.first to mutableListOf()
        threadTransformer.async {
            try {
                updateProse.invoke(proseId, revision, operations, output)
            } catch (t: Throwable) {
                nextUpdate = nextUpdate.first to mutableListOf()
                throw t
            }
        }
    }

    private var processingOperations: Boolean = false
    private var nextUpdate: Pair<Long, MutableList<Operation>> = startingRevisionNumber to mutableListOf()

    private val output = object : BulkUpdateProse.OutputPort {
        override suspend fun receiveBulkUpdateResponse(response: BulkUpdateProse.ResponseModel) {
            synchronized(this@EditProseControllerImpl) {
                processingOperations = false
                if (nextUpdate.second.isNotEmpty()) {
                    sendBatch(nextUpdate.first, nextUpdate.second)
                }
                nextUpdate = (response.events.lastOrNull()?.revision ?: nextUpdate.first) to mutableListOf()
            }
            updateProseOutput.receiveBulkUpdateResponse(response)
        }
    }

}