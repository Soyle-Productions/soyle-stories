package com.soyle.stories.storyevent.remove

import javafx.beans.value.ObservableValue

interface RemoveStoryEventConfirmationPromptLocale {

    fun confirmRemoveStoryEventFromProject(): ObservableValue<String>

    fun areYouSureYouWantToRemoveTheseStoryEventsFromTheProject(
        storyEventNames: List<String>
    ): ObservableValue<String>

    fun remove(): ObservableValue<String>

}
