package com.soyle.stories.scene.outline.remove.ramifications

import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import javafx.beans.value.ObservableValue
import javafx.scene.Node

interface UncoverStoryEventRamificationsReportLocale {
    fun uncoverStoryEventRamifications(): ObservableValue<String>

    fun implicitCharacterRemovedFromSceneMessage(
        effect: ImplicitCharacterRemovedFromScene
    ): ObservableMarkdownString

    fun noCharacterName(): ObservableValue<String>

}