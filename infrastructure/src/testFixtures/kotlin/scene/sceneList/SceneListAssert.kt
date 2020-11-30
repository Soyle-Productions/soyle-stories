package com.soyle.stories.desktop.view.scene.sceneList

import com.soyle.stories.scene.sceneList.SceneList
import org.junit.jupiter.api.Assertions

class SceneListAssert private constructor(private val driver: SceneListDriver) {

    companion object {
        fun assertThat(sceneList: SceneList, assertions: SceneListAssert.() -> Unit) {
            SceneListAssert(SceneListDriver(sceneList)).assertions()
        }
    }

    fun hasSceneNamed(sceneName: String)
    {
        Assertions.assertNotNull(driver.getSceneItem(sceneName)) { "Scene List does not contain scene named $sceneName" }
    }

    fun doesNotHaveSceneNamed(sceneName: String)
    {
        Assertions.assertNull(driver.getSceneItem(sceneName)) { "Scene List still contains scene named $sceneName" }
    }
}