package com.soyle.stories.desktop.locale.en

import com.soyle.stories.desktop.locale.SoyleMessageBundle
import java.util.*

object EnglishMessages : SoyleMessageBundle {

    override val locale: Locale = Locale.ENGLISH


    // Generic Words
    override val cancel: String = "Cancel"
    override val confirm: String = "Confirm"
    override val create: String = "Create"
    override val description: String = "Description"
    override val loading: String = "Loading"
    override val name: String = "Name"
    override val options: String = "Options"
    override val remove: String = "Remove"
    override val replaceWith: String = "Replace With..."
    override val retry: String = "Retry"

    // Characters
    override val addCharacter: String = "Add Character"
    override val characterRemovedFromStory: String = "Character has been removed from the story"
    override val noCharacterName: String = "Character name is unknown"
    override val removeCharacterFromStoryRamifications: String = "Ramifications: Remove Character from Story"

    // Locations
    override val createLocation: String = "Create Location"
    override val newLocation: String = "New Location"
    override val locationDetailsToolName: String = "Location Details Tool - {0}"
    override val useLocation: String = "Use Location"
    override val pleaseProvideALocationName: String = "Please provide a location name."
    // Locations -> Scenes
    override val hostScene: String = "Host Scene"
    override val scenesHostedInLocation: String = "Scenes Hosted in Location"
    override val hostSceneInLocationInvitationMessage: String =
        "Nothing currently happens here.  Sounds pretty boring.  Maybe you should spice this place up by adding a scene or five?"
    override val allExistingScenesInProjectHaveBeenHosted: String = "All Existing Scenes in Project Have Been Hosted"
    override val allExistingLocationsInProjectHaveBeenUsed: String = "All existing locations in project have been used"

    // Scenes
    override val createScene: String = "Create Scene"
    override val noSceneSelected: String = "No Scene Selected"
    override val selectedScene: String = "Selected Scene: %s"
    override val removeFromScene: String = "Remove From Scene"
    // Scenes -> Characters
    override val sceneCharactersToolTitle: String = "Scene Characters"
    override val confirmRemoveCharacterFromSceneTitle: String = "Confirm Remove Character from Scene"
    override val confirmRemoveCharacterFromSceneMessage: String ="Are you sure you want to remove %s from %s?"
    override val characterNotInvolvedInAnyStoryEvents: String = "Character is not covered by any story events in this scene"

    override val effect_scene_characters_implicitCharacterWillBeRemoved = """
        [{0}]({1}) will be removed from the "[{2}]({3})" scene
    """.trimIndent()
    override val effect_scene_characters_sceneWillBeInconsistentDueToIncludedCharacterRemoved = """
        [{0}]({1}) being removed will make the "[{2}]({3})" scene inconsistent because [{0}]({1}) will still be included in it.
    """.trimIndent()
    // Scenes -> Locations
    override val sceneSettings: String = "Scene Settings"
    override val useLocationsAsSceneSetting: String = "Use locations as a scene setting"
    override val sceneSettingToolTitle: String
        get() = sceneSettings
    override val noSceneSelected_inviteMessage = """
        No scene has been selected to use locations.  Click on a scene in the [Scene List](Tool(SceneList)) 
        or click anywhere inside of an open Scene Editor to **select** a scene and see what locations are being used.
    """.trimIndent()
    override val noLocationUsedInScene = """
        When you <span class="mention">@mention</span> a location in the scene, you can choose to use the location as a setting in the scene.  
        However, you can also choose to use a location as a setting in this scene by clicking the button below.
    """.trimIndent()
    // Scenes -> Story Events
    override val confirmRemoveStoryEventFromScene: String = "Confirm Remove Story Event from Scene"
    override val areYouSureYouWantToRemoveTheStoryEventFromTheScene: String = "Are you sure you want to remove %s from %s?"

    // Story Events
    override val confirmRemoveStoryEventFromProject: String = "Confirm Remove Story Event from Project"
    override val areYouSureYouWantToRemoveStoryEventFromProject: String =
        """Are you sure you want to remove {0, choice, 1#this story event|1<these story events} from the project?"""





    override val failedToLoadUsedLocations: String = "Failed to load used locations"
    override val locationHasBeenRemovedFromStory: String = "Location has been removed from the story"
    override val addValue: String = "Add Value"
    override val createNewValueWeb: String = "Create New Value Web"
    override val createOppositionValue: String = "Create Opposition Value"
    override val themeHasNoValueWebs: String = "Theme Has No Value Webs"
    override val nameCannotBeBlank: String = "Name Cannot Be Blank"
    override val opponentCharacter: String = "Opponent to Inciting Character"
    override val incitingCharacter: String = "Inciting Character"
    override val makeIncitingCharacter: String = "Make Inciting Character"
    override val removeIncitingCharacter: String = "Remove Inciting Character"
    override val makeOpponentCharacter: String = "Make Opponent Character"
    override val removeOpponentCharacter: String = "Remove Opponent Character"
    override val doNotShowDialogAgain: String = "Do Not Show Again"
    override val ramifications: String = "Ramifications"
    override val confirmRemoveCharacterFromStory: String = "Confirm Remove Character from Story"
    override val confirmRemoveCharacterFromStoryMesssage: String = "Are you should you want to remove %s from the story?"
    override val confirmRemoveCharacterFromStoryEvent: String = "Confirm Remove Character from Story Event"
    override val areYouSureYouWantToRemoveTheCharacterFromTheStoryEvent: String = "Are you sure you want to remove %s from %s?"
    override val uncoverStoryEventRamifications: String = "Ramifications: Uncover Story Event"


}