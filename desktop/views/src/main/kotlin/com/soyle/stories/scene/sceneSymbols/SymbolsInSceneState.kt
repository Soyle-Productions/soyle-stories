package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.common.ProjectScopedModel
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.items.SceneItemViewModel
import javafx.beans.property.SimpleObjectProperty

class SymbolsInSceneState : ProjectScopedModel<SymbolsInSceneViewModel>() {

    val targetScene = SimpleObjectProperty<Triple<Scene.Id, Prose.Id, String>?>(null)
    val themesInScene = bind(SymbolsInSceneViewModel::themesInScene)
    val availableSymbols = bind(SymbolsInSceneViewModel::availableThemesToTrack)

    override fun viewModel(): SymbolsInSceneViewModel? {
        return item?.copy(
            targetScene = targetScene.value
        )
    }

}