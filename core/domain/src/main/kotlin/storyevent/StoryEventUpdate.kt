package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.entities.updates.Update
import com.soyle.stories.domain.storyevent.events.StoryEventChange

sealed class StoryEventUpdate<out E : StoryEventChange> : Update<StoryEvent> {

    abstract val storyEvent: StoryEvent
    override operator fun component1() = storyEvent
}

class Successful<E : StoryEventChange>(
    override val storyEvent: StoryEvent,
    override val change: E
) : StoryEventUpdate<E>(), Update.Successful<StoryEvent, E> {
    override fun component2(): E = change
}
typealias SuccessfulStoryEventUpdate<E> = Successful<E>

class UnSuccessful(
    override val storyEvent: StoryEvent,
    override val reason: Throwable? = null
    ) : StoryEventUpdate<Nothing>(), Update.UnSuccessful<StoryEvent>
typealias UnSuccessfulStoryEventUpdate = UnSuccessful