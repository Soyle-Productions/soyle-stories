package com.soyle.stories.usecase.prose.bulkUpdateProse

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.events.ProseEvent
import com.soyle.stories.usecase.prose.ProseDoesNotExist
import com.soyle.stories.usecase.prose.ProseRepository


class BulkUpdateProseUseCase(private val proseRepository: ProseRepository) : BulkUpdateProse {

    override suspend fun invoke(proseId: Prose.Id, revision: Long, operations: List<Operation>, output: BulkUpdateProse.OutputPort) {
        val prose = proseRepository.getProseById(proseId) ?: throw ProseDoesNotExist(proseId)
        val eventsSinceRevision = proseRepository.getProseEvents(prose.id, revision)
        val collectedEvents = mutableListOf<ProseEvent>()
        val updatedProse = operations.withEventsApplied(eventsSinceRevision).fold(prose) { nextProse, operation ->
            val update = when (operation) {
                is InsertText -> nextProse.withTextInserted(operation.text, operation.index)
                is MentionEntity -> nextProse.withEntityMentioned(operation.entityId, operation.index, operation.length)
            }
            collectedEvents.add(update.event)
            update.prose
        }
        proseRepository.addEvents(prose.id, collectedEvents)
        proseRepository.replaceProse(updatedProse)
        output.receiveBulkUpdateResponse(BulkUpdateProse.ResponseModel(collectedEvents))
    }

    private fun List<Operation>.withEventsApplied(events: List<ProseEvent>): List<Operation>
    {
        return mapNotNull {
            it.applyEvents(events)
        }
    }

    private fun Operation.applyEvents(events: List<ProseEvent>): Operation?
    {
        return events.fold<ProseEvent, Operation?>(this) { nextOperation, event ->
            nextOperation?.transform(event)
        }
    }

}