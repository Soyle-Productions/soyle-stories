package com.soyle.stories.character.delete

import com.soyle.stories.character.delete.ramifications.RemoveCharacterRamificationsReportLocale
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventPromptLocale

interface RemoveCharacterLocale {

    val confirmation: ConfirmDeleteCharacterPromptLocale
    val ramifications: RemoveCharacterRamificationsReportLocale

}