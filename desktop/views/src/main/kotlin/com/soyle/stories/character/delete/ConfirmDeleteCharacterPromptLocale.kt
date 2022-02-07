package com.soyle.stories.character.delete

import javafx.beans.value.ObservableValue

interface ConfirmDeleteCharacterPromptLocale {

    fun confirmDeleteCharacterTitle(): ObservableValue<String>
    fun confirmDeleteCharacterMessage(characterName: ObservableValue<String>): ObservableValue<String>

    fun remove(): ObservableValue<String>

}