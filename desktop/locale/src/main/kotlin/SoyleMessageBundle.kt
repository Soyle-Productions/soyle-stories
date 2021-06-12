package com.soyle.stories.desktop.locale

import java.util.*

interface SoyleMessageBundle {
    val locale: Locale

    val description: String
    val loading: String
    val createScene: String
    val hostScene: String
    val scenesHostedInLocation: String
    val hostSceneInLocationInvitationMessage: String
    val allExistingScenesInProjectHaveBeenHosted: String
    val locationDetailsToolName: String
}