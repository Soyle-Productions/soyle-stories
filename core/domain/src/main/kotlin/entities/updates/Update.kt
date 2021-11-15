package com.soyle.stories.domain.entities.updates

import com.soyle.stories.domain.entities.Entity

interface Update<out E> {
    operator fun component1(): E

    interface Successful<out E, out C> : Update<E> {
        val change: C
        operator fun component2(): C
    }
    interface UnSuccessful<out E> : Update<E> {
        val reason: Throwable?
    }
}

typealias SuccessfulUpdate<E, C> = Update.Successful<E, C>
typealias UnSuccessfulUpdate<E> = Update.UnSuccessful<E>