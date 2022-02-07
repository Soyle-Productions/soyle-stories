package com.soyle.stories.usecase.shared.repositories

import com.soyle.stories.domain.entities.Entity

interface Repository<ID, E: Entity<ID>> {

    fun startTransaction(): Transaction<ID, E>

}