package com.soyle.stories.repositories

import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.ProseEvent
import com.soyle.stories.prose.repositories.ProseRepository

class ProseRepositoryImpl : ProseRepository {

    private val proseTable = mutableMapOf<Prose.Id, Prose>()
    private val eventTable = mutableMapOf<Prose.Id, MutableList<ProseEvent>>()

    override suspend fun addProse(prose: Prose) {
        proseTable[prose.id] = prose
    }

    override suspend fun getProseById(proseId: Prose.Id): Prose? = proseTable[proseId]

    override suspend fun replaceProse(prose: Prose) {
        proseTable[prose.id] = prose
    }

    override suspend fun addEvents(proseId: Prose.Id, events: List<ProseEvent>) {
        eventTable.getOrPut(proseId) { mutableListOf() }.addAll(events)
    }

    override suspend fun getProseEvents(proseId: Prose.Id, sinceRevision: Long): List<ProseEvent> {
        val events = eventTable.getOrDefault(proseId, listOf())
        val firstEvent = events.firstOrNull() ?: return emptyList()
        val startingIndex = sinceRevision - firstEvent.revision
        return events.subList(startingIndex.toInt() + 1, events.size).toList()
    }

}