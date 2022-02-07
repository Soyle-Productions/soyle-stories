package com.soyle.stories.usecase.shared.repositories

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.validation.DuplicateOperationException
import com.soyle.stories.domain.validation.EntityNotFoundException

interface MutableTransaction<ID, E : Entity<ID>> : Transaction<ID, E> {

    /**
     * @throws DuplicateOperationException if an entity with the id has already been created
     */
    suspend fun MutableRepository<ID, E>.add(entity: E)

    /**
     * @throws EntityNotFoundException if an entity with the id does not already exist
     */
    suspend fun MutableRepository<ID, E>.save(entity: E)

}