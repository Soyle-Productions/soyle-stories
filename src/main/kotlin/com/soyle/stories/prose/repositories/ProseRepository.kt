package com.soyle.stories.prose.repositories

import com.soyle.stories.entities.MentionedEntityId
import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.ProseEvent

interface ProseRepository {
    suspend fun getProseById(proseId: Prose.Id): Prose?
    suspend fun addProse(prose: Prose)
    suspend fun replaceProse(prose: Prose)
    suspend fun replaceProse(allProse: List<Prose>)
    suspend fun addEvents(proseId: Prose.Id, events: List<ProseEvent>)
    suspend fun getProseEvents(proseId: Prose.Id, sinceRevision: Long): List<ProseEvent>
    suspend fun getProseThatMentionEntity(entityId: MentionedEntityId<*>): List<Prose>
}