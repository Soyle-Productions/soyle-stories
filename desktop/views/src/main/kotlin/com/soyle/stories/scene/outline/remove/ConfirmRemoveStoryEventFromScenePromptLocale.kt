package com.soyle.stories.scene.outline.remove

import javafx.beans.value.ObservableValue

interface ConfirmRemoveStoryEventFromScenePromptLocale {
    fun areYouSureYouWantToRemoveTheStoryEventFromTheScene(
        storyEventName: ObservableValue<String>,
        sceneName: ObservableValue<String>
    ): ObservableValue<String>

    fun confirmRemoveStoryEventFromScene(): ObservableValue<String>

    fun remove(): ObservableValue<String>
}