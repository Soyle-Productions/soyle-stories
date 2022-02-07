package com.soyle.stories.usecase.shared.repositories

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.validation.EntityNotFoundException

interface Transaction<ID, E: Entity<ID>> {

    suspend fun Repository<ID, E>.get(id: ID): E?

    /**
     * @throws EntityNotFoundException if the entity of type [E] was not found
     */
    suspend fun Repository<ID, E>.getOrError(id: ID): E

}