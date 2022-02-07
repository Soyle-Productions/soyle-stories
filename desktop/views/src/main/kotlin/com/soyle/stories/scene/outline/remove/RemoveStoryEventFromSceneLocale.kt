package com.soyle.stories.scene.outline.remove

import com.soyle.stories.scene.outline.remove.ramifications.UncoverStoryEventRamificationsReportLocale

interface RemoveStoryEventFromSceneLocale {

    val prompt: ConfirmRemoveStoryEventFromScenePromptLocale
    val ramifications: UncoverStoryEventRamificationsReportLocale

}