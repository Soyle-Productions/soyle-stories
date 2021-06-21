package com.soyle.stories.scene.setting

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene

interface SceneSettingViewListener {

    fun openSceneListTool()
    fun getLocationsUsedForSceneSetting(sceneId: Scene.Id)
    fun listAvailableLocationsToUse(sceneId: Scene.Id)
    fun useLocation(sceneId: Scene.Id, locationId: Location.Id)
    fun removeLocation(sceneId: Scene.Id, locationId: Location.Id)

}