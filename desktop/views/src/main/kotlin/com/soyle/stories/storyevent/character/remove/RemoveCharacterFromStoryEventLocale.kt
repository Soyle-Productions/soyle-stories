package com.soyle.stories.storyevent.character.remove

import com.soyle.stories.storyevent.character.remove.ramifications.RemoveCharacterFromStoryEventRamificationsReportLocale

interface RemoveCharacterFromStoryEventLocale {

    val confirmation: RemoveCharacterFromStoryEventPromptLocale
    val ramifications: RemoveCharacterFromStoryEventRamificationsReportLocale

}