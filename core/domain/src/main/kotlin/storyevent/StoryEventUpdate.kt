package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.storyevent.events.StoryEventChange

sealed class StoryEventUpdate<out E : StoryEventChange>() {

    abstract val storyEvent: StoryEvent
    operator fun component1() = storyEvent
}

class Successful<E : StoryEventChange>(
    override val storyEvent: StoryEvent,
    val change: E
) : StoryEventUpdate<E>() {
    operator fun component2() = change
}
typealias SuccessfulStoryEventUpdate<E> = Successful<E>

class UnSuccessful(override val storyEvent: StoryEvent) : StoryEventUpdate<Nothing>()
typealias UnSuccessfulStoryEventUpdate = UnSuccessful