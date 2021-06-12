package com.soyle.stories.desktop.view.location.details

import com.soyle.stories.desktop.view.location.details.`Location Details Access`.Companion.access
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.details.LocationDetailsView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class `Location Details View Assertions`(private val access: `Location Details Access`) {

    companion object {
        fun assertThat(view: LocationDetailsView, assertions: `Location Details View Assertions`.() -> Unit) {
            `Location Details View Assertions`(view.access()).assertions()
        }
        fun LocationDetailsView.assertThis(assertions: `Location Details View Assertions`.() -> Unit) =
            assertThat(this, assertions)
    }

    fun doesNotHaveSceneNamed(sceneName: String) {
        with(access) {
            assertNull(hostedScenesList?.hostedSceneItems?.find { it.text == sceneName })
        }
    }

    fun hasScene(sceneId: Scene.Id, expectedName: String) {
        val hostedScene = with(access) {
            hostedScenesList?.hostedSceneItems?.find { it.id == sceneId.toString() }
        } ?: throw AssertionError("Hosted Scene not listed $sceneId")
        assertEquals(expectedName, hostedScene.text)
    }

    fun hasAvailableSceneItem(sceneId: Scene.Id, expectedName: String) {
        val sceneItem = with(access) {
            hostSceneButton!!.availableSceneItems.find { it.id == sceneId.toString() }!!
        }
        assertEquals(expectedName, sceneItem.text)
    }

    fun doesNotHaveAvailableSceneItem(sceneId: Scene.Id) {
        val sceneItem = with(access) {
            hostSceneButton!!.availableSceneItems.find { it.id == sceneId.toString() }
        }
        assertNull(sceneItem)
    }
}
