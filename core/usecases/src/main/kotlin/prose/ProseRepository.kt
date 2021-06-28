package com.soyle.stories.usecase.prose

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseEvent

interface ProseRepository {
    suspend fun getProseById(proseId: Prose.Id): Prose?
    suspend fun getProseOrError(proseId: Prose.Id): Prose =
        getProseById(proseId) ?: throw ProseDoesNotExist(proseId)

    suspend fun addProse(prose: Prose)
    suspend fun replaceProse(prose: Prose)
    suspend fun replaceProse(allProse: List<Prose>)
    suspend fun addEvents(proseId: Prose.Id, events: List<ProseEvent>)
    suspend fun getProseEvents(proseId: Prose.Id, sinceRevision: Long): List<ProseEvent>
    suspend fun getProseThatMentionEntity(entityId: MentionedEntityId<*>): List<Prose>
}