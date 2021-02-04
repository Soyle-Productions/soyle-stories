package com.soyle.stories.storyevent

import java.util.*

abstract class StoryEventException : Exception()

class StoryEventCannotBeBlank: StoryEventException()
class StoryEventDoesNotExist(val storyEventId: UUID) : StoryEventException()
class CharacterNotInStoryEvent(val storyEventId: UUID, val characterId: UUID) : StoryEventException()