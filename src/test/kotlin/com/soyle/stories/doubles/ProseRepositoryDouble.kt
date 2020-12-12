package com.soyle.stories.doubles

import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.ProseEvent
import com.soyle.stories.prose.repositories.ProseRepository

class ProseRepositoryDouble(
    private val onCreateProse: (Prose) -> Unit = {},
    private val onReplaceProse: (Prose) -> Unit = {}
) : ProseRepository {

    private val prose = mutableMapOf<Prose.Id, Prose>()
    private val proseEvents = mutableMapOf<Prose.Id, MutableList<ProseEvent>>()

    fun givenProse(prose: Prose)
    {
        this.prose[prose.id] = prose
    }

    override suspend fun getProseById(proseId: Prose.Id): Prose? = prose[proseId]

    override suspend fun addProse(prose: Prose) {
        this.prose[prose.id] = prose
        onCreateProse(prose)
    }

    override suspend fun replaceProse(prose: Prose) {
        this.prose[prose.id] = prose
        onReplaceProse(prose)
    }

    override suspend fun getProseEvents(proseId: Prose.Id, sinceRevision: Long): List<ProseEvent> {
        return proseEvents.getOrDefault(proseId, listOf()).filter { it.revision > sinceRevision }
    }

    override suspend fun addEvents(proseId: Prose.Id, events: List<ProseEvent>) {
        proseEvents.getOrPut(proseId, ::mutableListOf).addAll(events)
    }

}