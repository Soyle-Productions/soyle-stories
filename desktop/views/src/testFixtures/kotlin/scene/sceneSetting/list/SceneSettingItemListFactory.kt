package com.soyle.stories.desktop.view.scene.sceneSetting.list

import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.DetectInconsistenciesInSceneSettingsControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.ListLocationsInSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.ListLocationsToUseInSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.item.SceneSettingItemFactory
import com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton.UseLocationButtonFactory
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.deleteLocation.DeletedLocationNotifier
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneNotifier
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneNotifier
import com.soyle.stories.scene.setting.list.SceneSettingItemList
import com.soyle.stories.scene.setting.list.SceneSettingItemListModel
import javafx.beans.value.ObservableValue

class SceneSettingItemListFactory(
    var onInvoke: (Scene.Id) -> Unit = {},
    val listLocationsInSceneController: ListLocationsInSceneControllerDouble = ListLocationsInSceneControllerDouble()
) : SceneSettingItemList.Factory {

    override fun invoke(sceneId: Scene.Id): SceneSettingItemList {
        onInvoke(sceneId)
        return SceneSettingItemList(
            sceneId,
            SceneSettingItemListLocaleMock(),
            listLocationsInSceneController,
            DetectInconsistenciesInSceneSettingsControllerDouble(),
            LocationRemovedFromSceneNotifier(),
            LocationUsedInSceneNotifier(),
            DeletedLocationNotifier(),
            SceneSettingItemFactory(),
            UseLocationButtonFactory()
        )
    }
}