package com.soyle.stories.prose.repositories

import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.ProseDoesNotExist

suspend fun ProseRepository.getProseOrError(proseId: Prose.Id): Prose =
    getProseById(proseId) ?: throw ProseDoesNotExist(proseId)