package com.soyle.stories.prose.repositories

import com.soyle.stories.entities.Prose

interface ProseRepository {
    suspend fun getProseById(proseId: Prose.Id): Prose?
    suspend fun addProse(prose: Prose)
}