package com.soyle.stories.usecase.shared.repositories

import com.soyle.stories.domain.entities.Entity

interface MutableRepository<ID, E: Entity<ID>> : Repository<ID, E> {

    override fun startTransaction(): MutableTransaction<ID, E>

}