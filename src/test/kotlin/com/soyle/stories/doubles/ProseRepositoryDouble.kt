package com.soyle.stories.doubles

import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.repositories.ProseRepository

class ProseRepositoryDouble(
    private val onCreateProse: (Prose) -> Unit = {}
) : ProseRepository {

    private val prose = mutableMapOf<Prose.Id, Prose>()

    override suspend fun getProseById(proseId: Prose.Id): Prose? = prose[proseId]

    override suspend fun addProse(prose: Prose) {
        this.prose[prose.id] = prose
        onCreateProse(prose)
    }

}