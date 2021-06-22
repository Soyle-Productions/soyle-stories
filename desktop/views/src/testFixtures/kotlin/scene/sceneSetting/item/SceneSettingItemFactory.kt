package com.soyle.stories.desktop.view.scene.sceneSetting.item

import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.RemoveLocationFromSceneControllerDouble
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesNotifier
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedNotifier
import com.soyle.stories.scene.setting.list.item.SceneSettingItemModel
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView

class SceneSettingItemFactory(
    private val onInvoke: (SceneSettingItemModel) -> Unit = {}
) : SceneSettingItemView.Factory {

    override fun invoke(model: SceneSettingItemModel): SceneSettingItemView {
        onInvoke(model)
        return SceneSettingItemView(
            model,
            SceneSettingItemLocaleMock(),
            RemoveLocationFromSceneControllerDouble(),
            SceneSettingLocationRenamedNotifier(),
            SceneInconsistenciesNotifier()
        )
    }
}