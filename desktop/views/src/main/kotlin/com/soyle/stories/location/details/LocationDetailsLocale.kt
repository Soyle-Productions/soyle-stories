package com.soyle.stories.location.details

import javafx.beans.value.ObservableValue

interface LocationDetailsLocale {
    val description: ObservableValue<String>
    val scenesHostedInLocation: ObservableValue<String>
    val hostScene: ObservableValue<String>
    val loading: ObservableValue<String>
    val createScene: ObservableValue<String>

    val hostSceneInLocationInvitationMessage: ObservableValue<String>
    val allExistingScenesInProjectHaveBeenHosted: ObservableValue<String>

    fun locationDetailsToolName(locationName: ObservableValue<String>): ObservableValue<String>
}