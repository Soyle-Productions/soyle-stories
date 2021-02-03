package com.soyle.stories.desktop.view.scene.sceneList

import com.soyle.stories.scene.sceneList.SceneListView
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class SceneListAssert private constructor(private val driver: SceneListDriver) {

    companion object {
        fun assertThat(sceneList: SceneListView, assertions: SceneListAssert.() -> Unit) {
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

    fun doesNotIndicateSceneHasAnIssue(sceneName: String)
    {
        assertFalse(driver.getSceneItem(sceneName)?.value?.hasProblem == true)
    }

    fun indicatesSceneHasAnIssue(sceneName: String)
    {
        assertTrue(driver.getSceneItem(sceneName)?.value?.hasProblem == true)
    }
}