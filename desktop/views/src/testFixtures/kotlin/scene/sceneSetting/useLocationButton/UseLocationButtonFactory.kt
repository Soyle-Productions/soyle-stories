package com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton

import com.soyle.stories.desktop.view.location.create.CreateLocationDialogFactory
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.LinkLocationToSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.ListLocationsToUseInSceneControllerDouble
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.setting.list.useLocationButton.AvailableSceneSettingModel
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton
import javafx.beans.binding.ListExpression

class UseLocationButtonFactory(
    val makeCreateLocationDialog: CreateLocationDialogFactory = CreateLocationDialogFactory(),
    val listLocationsToUseInSceneController: ListLocationsToUseInSceneControllerDouble = ListLocationsToUseInSceneControllerDouble(),
    val linkLocationToSceneController: LinkLocationToSceneControllerDouble = LinkLocationToSceneControllerDouble(),
    private val onInvoke: (Scene.Id) -> Unit = {}
) : UseLocationButton.Factory {

    override fun invoke(
        sceneId: Scene.Id,
    ): UseLocationButton {
        onInvoke(sceneId)
        return UseLocationButton(
            sceneId,
            UseLocationButtonLocaleMock(),
            makeCreateLocationDialog,
            listLocationsToUseInSceneController,
            linkLocationToSceneController
        )
    }
}