package com.soyle.stories.character.delete.ramifications

import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.usecase.scene.character.effects.CharacterInSceneEffect
import javafx.beans.value.ObservableValue
import javafx.scene.Node

interface RemoveCharacterRamificationsReportLocale {

    fun removeCharacterFromStoryRamifications(): ObservableValue<String>

    fun characterInSceneEffectMessage(effect: CharacterInSceneEffect): ObservableMarkdownString

    fun noCharacterName(): ObservableValue<String>

}