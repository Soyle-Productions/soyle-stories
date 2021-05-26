package com.soyle.stories.domain.location

sealed class LocationUpdate<out Event> {
    abstract val location: Location
    operator fun component1() = location
}

class NoUpdate(override val location: Location, val reason: Any? = null) : LocationUpdate<Nothing>()
{
    operator fun component2() = reason
}

class Updated<Event>(override val location: Location, val event: Event) : LocationUpdate<Event>()
{
    operator fun component2() = event
}