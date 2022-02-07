package com.soyle.stories.storyevent

import com.soyle.stories.storyevent.character.StoryEventCharactersLocale
import com.soyle.stories.storyevent.remove.RemoveStoryEventLocale

interface StoryEventLocale {

    val remove: RemoveStoryEventLocale

    val characters: StoryEventCharactersLocale

}