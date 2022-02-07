package com.soyle.stories.scene.characters.remove

import javafx.beans.value.ObservableValue

interface ConfirmRemoveCharacterFromScenePromptLocale {

    fun confirmRemoveCharacterFromSceneTitle(): ObservableValue<String>
    fun confirmRemoveCharacterFromSceneMessage(characterName: ObservableValue<String>, sceneName: ObservableValue<String>): ObservableValue<String>

    fun remove(): ObservableValue<String>

}