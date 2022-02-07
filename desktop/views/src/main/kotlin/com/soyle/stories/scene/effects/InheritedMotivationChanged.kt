package com.soyle.stories.scene.effects

import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.common.markdown.markdown
import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.layout.VBox
import tornadofx.Scope
import tornadofx.addClass

interface InheritedMotivationChangedLocale {

    fun willNoLongerHaveMotivationInScene(
        motivation: ObservableValue<String>,
        sceneName: ObservableValue<String>,
        sceneId: Scene.Id
    ): ObservableMarkdownString

    fun willInheritMotivation(
        motivation: ObservableValue<String>,
        sourceSceneName: ObservableValue<String>,
        sourceSceneId: Scene.Id,
        sceneName: ObservableValue<String>,
        sceneId: Scene.Id
    ): ObservableMarkdownString

}

fun Scope.InheritedCharacterMotivationWillBeCleared(
    viewModel: InheritedMotivationWillBeClearedViewModel,
    locale: InheritedMotivationChangedLocale = get()
): Node {
    return VBox().apply {
        fieldLabel(viewModel.characterName)
        markdown(
            locale.willNoLongerHaveMotivationInScene(
                viewModel.motivation,
                viewModel.sceneName,
                viewModel.sceneId
            )
        ).apply { addClass("message") }
    }
}

fun Scope.CharacterWillGainInheritedMotivation(
    viewModel: CharacterWillGainInheritedMotivationViewModel,
    locale: InheritedMotivationChangedLocale = get()
): Node {
    return VBox().apply {
        fieldLabel(viewModel.characterName)
        markdown(
            locale.willNoLongerHaveMotivationInScene(
                viewModel.previousMotivation,
                viewModel.sceneName,
                viewModel.sceneId
            )
        ).apply { addClass("message") }
        markdown(
            locale.willInheritMotivation(
                viewModel.newMotivation,
                viewModel.newSourceSceneName,
                viewModel.newSourceId,
                viewModel.sceneName,
                viewModel.newSourceId
            )
        ).apply { addClass("message") }
    }
}