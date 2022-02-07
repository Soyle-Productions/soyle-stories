package com.soyle.stories.storyevent.character.remove.ramifications

import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.usecase.scene.character.effects.CharacterInSceneEffect
import javafx.beans.value.ObservableValue
import javafx.scene.Node

interface RemoveCharacterFromStoryEventRamificationsReportLocale {

    fun characterInSceneEffectMessage(effect: CharacterInSceneEffect): ObservableMarkdownString

    fun noCharacterName(): ObservableValue<String>

}