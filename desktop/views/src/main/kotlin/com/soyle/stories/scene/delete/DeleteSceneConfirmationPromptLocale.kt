package com.soyle.stories.scene.delete

import javafx.beans.value.ObservableValue

interface DeleteSceneConfirmationPromptLocale {

    val title: ObservableValue<String>
    fun message(sceneName: ObservableValue<String>): ObservableValue<String>

    val remove: ObservableValue<String>

}