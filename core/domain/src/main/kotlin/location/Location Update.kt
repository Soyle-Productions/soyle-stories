package com.soyle.stories.domain.location

import com.soyle.stories.domain.location.events.HostedSceneRenamed

sealed class LocationUpdate<out Event> {

    abstract val location: Location
    operator fun component1() = location
}

class NoUpdate(override val location: Location, val reason: Any? = null) : LocationUpdate<Nothing>() {

    operator fun component2() = reason
}

class Updated<Event>(override val location: Location, val event: Event) : LocationUpdate<Event>() {

    operator fun component2() = event
}

inline fun <E> List<LocationUpdate<E>>.locations() = map(LocationUpdate<*>::location)

inline fun <E> List<LocationUpdate<E>>.events() = asSequence()
    .filterIsInstance<Updated<E>>()
    .map(Updated<E>::event)
    .toList()
