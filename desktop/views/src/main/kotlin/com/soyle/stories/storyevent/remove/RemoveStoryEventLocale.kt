package com.soyle.stories.storyevent.remove

import com.soyle.stories.storyevent.remove.ramifications.RemoveStoryEventFromStoryRamificationsReportLocale

interface RemoveStoryEventLocale {

    val prompt: RemoveStoryEventConfirmationPromptLocale
    val ramifications: RemoveStoryEventFromStoryRamificationsReportLocale

}