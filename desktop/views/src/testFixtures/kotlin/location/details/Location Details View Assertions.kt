package com.soyle.stories.desktop.view.location.details

import com.soyle.stories.desktop.view.location.details.`Location Details View Access`.Companion.access
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.locationDetails.LocationDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class `Location Details View Assertions`(private val access: `Location Details View Access`) {

    companion object {
        fun assertThat(view: LocationDetails, assertions: `Location Details View Assertions`.() -> Unit) {
            `Location Details View Assertions`(view.access()).assertions()
        }
        fun LocationDetails.assertThis(assertions: `Location Details View Assertions`.() -> Unit) = assertThat(this, assertions)
    }

    fun doesNotHaveSceneNamed(sceneName: String) {
        assertNull(access.getHostedSceneByName(sceneName))
    }

    fun hasScene(sceneId: Scene.Id, expectedName: String) {
        assertEquals(expectedName, access.getHostedScene(sceneId)!!.text)
    }

    fun hasAvailableSceneItem(sceneId: Scene.Id, expectedName: String) {
        val sceneItem =with (access) {
            availableScenesToHost!!.getSceneItem(sceneId)!!
        }
        assertEquals(expectedName, sceneItem.text)
    }

    fun doesNotHaveAvailableSceneItem(sceneId: Scene.Id) {
        val sceneItem = with (access) {
            availableScenesToHost?.getSceneItem(sceneId)
        }
        assertNull(sceneItem)
    }

}