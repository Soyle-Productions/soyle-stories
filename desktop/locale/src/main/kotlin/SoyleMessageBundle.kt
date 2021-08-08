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
    val noLocationUsedInSceneMessage: List<MessageSegment>
    val noSceneSelectedInviteMessage: List<MessageSegment>
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

    sealed class MessageSegment {
        abstract val message: String

        class Text(override val message: String) : MessageSegment()
        class Link(override val message: String) : MessageSegment()
        class Warning(override val message: String) : MessageSegment()
        class Mention(override val message: String) : MessageSegment()
    }
}