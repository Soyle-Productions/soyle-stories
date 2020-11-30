package com.soyle.stories.repositories

import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.repositories.ProseRepository

class ProseRepositoryImpl : ProseRepository {

    private val proseTable = mutableMapOf<Prose.Id, Prose>()

    override suspend fun addProse(prose: Prose) {
        proseTable[prose.id] = prose
    }

    override suspend fun getProseById(proseId: Prose.Id): Prose? = proseTable[proseId]

}