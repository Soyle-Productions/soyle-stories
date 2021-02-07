package com.soyle.stories.usecase.repositories

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseEvent
import com.soyle.stories.usecase.prose.ProseRepository

class ProseRepositoryDouble(
    private val onCreateProse: (Prose) -> Unit = {},
    private val onReplaceProse: (Prose) -> Unit = {},
    private val onAddEvents: (Prose.Id, List<ProseEvent>) -> Unit = { _, _ -> }
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

    override suspend fun replaceProse(allProse: List<Prose>) {
        allProse.forEach {
            prose[it.id] = it
            onReplaceProse(it)
        }
    }

    override suspend fun getProseThatMentionEntity(entityId: MentionedEntityId<*>): List<Prose> {
        return prose.values.filter { mention -> mention.mentions.any { it.entityId == entityId } }
    }

    override suspend fun getProseEvents(proseId: Prose.Id, sinceRevision: Long): List<ProseEvent> {
        return proseEvents.getOrDefault(proseId, listOf()).filter { it.revision > sinceRevision }
    }

    override suspend fun addEvents(proseId: Prose.Id, events: List<ProseEvent>) {
        proseEvents.getOrPut(proseId, ::mutableListOf).addAll(events)
        onAddEvents(proseId, events)
    }

}