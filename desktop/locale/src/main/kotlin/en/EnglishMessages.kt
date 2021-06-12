package com.soyle.stories.desktop.locale.en

import com.soyle.stories.desktop.locale.SoyleMessageBundle
import java.util.*

object EnglishMessages : SoyleMessageBundle {

    override val locale: Locale = Locale.ENGLISH

    override val description: String = "Description"
    override val loading: String = "Loading"
    override val createScene: String = "Create Scene"
    override val hostScene: String = "Host Scene"
    override val scenesHostedInLocation: String = "Scenes Hosted in Location"
    override val hostSceneInLocationInvitationMessage: String =
        "Nothing currently happens here.  Sounds pretty boring.  Maybe you should spice this place up by adding a scene or five?"
    override val allExistingScenesInProjectHaveBeenHosted: String = "All Existing Scenes in Project Have Been Hosted"
    override val locationDetailsToolName: String = "Location Details Tool - %s"

}