package com.soyle.stories

import com.soyle.stories.character.CharacterLocale
import com.soyle.stories.location.LocationLocale
import com.soyle.stories.ramifications.RamificationsLocale
import com.soyle.stories.scene.SceneLocale
import com.soyle.stories.scene.delete.RemoveSceneLocale
import com.soyle.stories.storyevent.StoryEventLocale

interface Locale {
    val scenes: SceneLocale
    val locations: LocationLocale
    val characters: CharacterLocale
    val storyEvents: StoryEventLocale

    val ramifications: RamificationsLocale
}