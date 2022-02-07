@file:Suppress("PropertyName")

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
    val noLocationUsedInScene: String
    val noSceneSelected_inviteMessage: String
    val noSceneSelected: String
    val sceneSettingToolTitle: String
    val selectedScene: String
    val useLocationsAsSceneSetting: String
    val failedToLoadUsedLocations: String
    val retry: String
    val sceneSettings: String
    val useLocation: String
    val allExistingLocationsInProjectHaveBeenUsed: String
    val createLocation: String
    val locationHasBeenRemovedFromStory: String
    val name: String
    val create: String
    val cancel: String
    val newLocation: String
    val pleaseProvideALocationName: String
    val replaceWith: String
    val removeFromScene: String
    val addValue: String
    val createNewValueWeb: String
    val createOppositionValue: String
    val themeHasNoValueWebs: String
    val nameCannotBeBlank: String
    val sceneCharactersToolTitle: String
    val addCharacter: String
    val options: String
    val opponentCharacter: String
    val incitingCharacter: String
    val makeIncitingCharacter: String
    val removeIncitingCharacter: String
    val makeOpponentCharacter: String
    val removeOpponentCharacter: String
    val remove: String
    val confirmRemoveCharacterFromSceneTitle: String
    val confirmRemoveCharacterFromSceneMessage: String
    val confirm: String
    val doNotShowDialogAgain: String
    val ramifications: String
    val characterRemovedFromStory: String
    val characterNotInvolvedInAnyStoryEvents: String
    val confirmRemoveCharacterFromStory: String
    val confirmRemoveCharacterFromStoryMesssage: String
    val noCharacterName: String
    val areYouSureYouWantToRemoveTheCharacterFromTheStoryEvent: String
    val areYouSureYouWantToRemoveTheStoryEventFromTheScene: String
    val confirmRemoveCharacterFromStoryEvent: String
    val confirmRemoveStoryEventFromScene: String
    val uncoverStoryEventRamifications: String
    val removeCharacterFromStoryRamifications: String
    val confirmRemoveStoryEventFromProject: String
    val areYouSureYouWantToRemoveStoryEventFromProject: String

    /**
     * Should format with [Character.Id], [String] as character name, [Scene.Id], and [String] as scene name
     */
    val effect_scene_characters_implicitCharacterWillBeRemoved: String
    /**
     * Should format with [Character.Id], [String] as character name, [Scene.Id], and [String] as scene name
     */
    val effect_scene_characters_sceneWillBeInconsistentDueToIncludedCharacterRemoved: String

    sealed class MessageSegment {
        abstract val message: String

        class Text(override val message: String) : MessageSegment()
        class Link(override val message: String) : MessageSegment()
        class Warning(override val message: String) : MessageSegment()
        class Mention(override val message: String) : MessageSegment()
    }
}