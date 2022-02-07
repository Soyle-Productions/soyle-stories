package com.soyle.stories.scene

import com.soyle.stories.scene.characters.SceneCharactersLocale
import com.soyle.stories.scene.delete.RemoveSceneLocale
import com.soyle.stories.scene.effects.InheritedMotivationChangedLocale
import com.soyle.stories.scene.outline.SceneOutlineLocale
import com.soyle.stories.scene.setting.SceneSettingToolLocale
import javafx.beans.value.ObservableValue

interface SceneLocale {

    val remove: RemoveSceneLocale

    val tool: CommonToolLocale

    interface CommonToolLocale {
        val noSceneSelected: ObservableValue<String>
        fun selectedScene(sceneName: ObservableValue<String>): ObservableValue<String>
    }

    val characters: SceneCharactersLocale
    val locations: SceneSettingToolLocale
    val storyEvents: SceneOutlineLocale

    val effects: InheritedMotivationChangedLocale
}