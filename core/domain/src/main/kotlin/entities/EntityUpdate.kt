package com.soyle.stories.domain.entities

interface EntityUpdate<out E : Entity<*>> {
    operator fun component1(): E
}