package com.soyle.stories.storyevent.character.remove

import javafx.beans.value.ObservableValue

interface RemoveCharacterFromStoryEventPromptLocale {

    fun confirmRemoveCharacterFromStoryEvent(): ObservableValue<String>
    fun areYouSureYouWantToRemoveTheCharacterFromTheStoryEvent(
        characterName: ObservableValue<String>,
        storyEventName: ObservableValue<String>
    ): ObservableValue<String>

    fun remove(): ObservableValue<String>

}