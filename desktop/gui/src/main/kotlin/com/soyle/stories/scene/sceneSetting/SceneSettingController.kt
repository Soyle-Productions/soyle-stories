package com.soyle.stories.scene.sceneSetting

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.gui.View
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneReceiver
import com.soyle.stories.scene.locationsInScene.listLocationsInScene.ListLocationsInSceneController
import com.soyle.stories.scene.locationsInScene.listLocationsToUse.ListLocationsToUseInSceneController
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneReceiver
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneController

class SceneSettingController private constructor(
    private val openToolController: OpenToolController,
    private val listLocationsInSceneController: ListLocationsInSceneController,
    private val linkLocationToSceneController: LinkLocationToSceneController,
    private val listLocationsToUseInSceneController: ListLocationsToUseInSceneController,
    private val removeLocationFromSceneController: RemoveLocationFromSceneController,
    private val presenter: SceneSettingPresenter
) : SceneSettingViewListener {

    interface Dependencies {
        val openToolController: OpenToolController
        val listLocationsInSceneController: ListLocationsInSceneController
        val linkLocationToSceneController: LinkLocationToSceneController
        val listLocationsToUseInSceneController: ListLocationsToUseInSceneController
        val removeLocationFromSceneController: RemoveLocationFromSceneController

        val locationRemovedFromSceneNotifier: Notifier<LocationRemovedFromSceneReceiver>
        val locationUsedInSceneNotifier: Notifier<LocationUsedInSceneReceiver>
    }

    constructor(
        dependencies: Dependencies,
        view: View.Nullable<SceneSettingViewModel>
    ) : this(
        dependencies.openToolController,
        dependencies.listLocationsInSceneController,
        dependencies.linkLocationToSceneController,
        dependencies.listLocationsToUseInSceneController,
        dependencies.removeLocationFromSceneController,
        SceneSettingPresenter(
            view,
            dependencies.locationRemovedFromSceneNotifier,
            dependencies.locationUsedInSceneNotifier
        )
    )

    override fun openSceneListTool() {
        openToolController.openSceneList()
    }

    override fun getLocationsUsedForSceneSetting(sceneId: Scene.Id) {
        listLocationsInSceneController.listLocationsInScene(sceneId, presenter)
    }

    override fun listAvailableLocationsToUse(sceneId: Scene.Id) {
        listLocationsToUseInSceneController.listLocationsToUse(sceneId, presenter)
    }

    override fun useLocation(sceneId: Scene.Id, locationId: Location.Id) {
        linkLocationToSceneController.linkLocationToScene(sceneId, locationId)
    }

    override fun removeLocation(sceneId: Scene.Id, locationId: Location.Id) {
        removeLocationFromSceneController.removeLocation(sceneId, locationId)
    }

}