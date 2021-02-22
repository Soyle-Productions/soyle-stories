package com.soyle.stories.scene.sceneSetting

import com.soyle.stories.common.ProjectScopedModel
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.items.SceneItemViewModel
import javafx.beans.property.SimpleObjectProperty
import java.util.*

class SceneSettingState : ProjectScopedModel<SceneSettingViewModel>() {

    val targetScene = SimpleObjectProperty<SceneItemViewModel?>(null)
    val availableLocations = bind(SceneSettingViewModel::availableLocations)
    val usedLocations = bind(SceneSettingViewModel::usedLocations)

    override fun viewModel(): SceneSettingViewModel? {
        return item?.copy(
            targetSceneId = targetScene.value?.id?.let(UUID::fromString)?.let(Scene::Id),
            availableLocations = availableLocations.value
        )
    }

}