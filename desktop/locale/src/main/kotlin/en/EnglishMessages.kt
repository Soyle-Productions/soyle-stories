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
    override val allExistingLocationsInProjectHaveBeenUsed: String = "All existing locations in project have been used"
    override val createLocation: String = "Create Location"
    override val failedToLoadUsedLocations: String = "Failed to load used locations"
    override val locationHasBeenRemovedFromStory: String = "Location has been removed from the story"
    override val noSceneSelected: String = "No Scene Selected"
    override val retry: String = "Retry"
    override val sceneSettings: String = "Scene Settings"
    override val sceneSettingToolTitle: String
        get() = sceneSettings
    override val selectedScene: String = "Selected Scene"
    override val useLocation: String = "Use Location"
    override val useLocationsAsSceneSetting: String = "Use locations as a scene setting"
    override val noSceneSelectedInviteMessage: List<SoyleMessageBundle.MessageSegment> = listOf(
        SoyleMessageBundle.MessageSegment.Text("No scene has been selected to use locations.  Click on a scene in the "),
        SoyleMessageBundle.MessageSegment.Link("Scene List"),
        SoyleMessageBundle.MessageSegment.Text(" or click anywhere inside of an open Scene Editor to "),
        SoyleMessageBundle.MessageSegment.Warning("select"),
        SoyleMessageBundle.MessageSegment.Text(" a scene and see what locations are being used.")
    )
    override val noLocationUsedInSceneMessage: List<SoyleMessageBundle.MessageSegment> = listOf(
        SoyleMessageBundle.MessageSegment.Text("When you "),
        SoyleMessageBundle.MessageSegment.Mention("@mention"),
        SoyleMessageBundle.MessageSegment.Text(" a location in the scene, you can choose to use the location as a setting in the scene.  However, you can also choose to use a location as a setting in this scene by clicking the button below."),
    )

}