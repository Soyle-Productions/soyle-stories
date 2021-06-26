package com.soyle.stories.desktop.view.scene.sceneSetting

import com.soyle.stories.common.components.ComponentsStyles.Companion.hasProblem
import com.soyle.stories.desktop.view.common.components.dataDisplay.`Chip Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneSetting.`Scene Setting Tool Root Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneSetting.item.`Scene Setting Item Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneSetting.list.`Scene Setting Item List Access`.Companion.access
import com.soyle.stories.domain.location.Location
import com.soyle.stories.scene.setting.SceneSettingToolRoot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.fail
import tornadofx.hasClass

class SceneSettingAssertions private constructor(private val access: `Scene Setting Tool Root Access`) {
    companion object {

        @JvmName("thenAssertThat")
        fun SceneSettingToolRoot.assertThat(assertions: SceneSettingAssertions.() -> Unit) {
            SceneSettingAssertions(access()).assertions()
        }

        fun assertThat(view: SceneSettingToolRoot, assertions: SceneSettingAssertions.() -> Unit) {
            SceneSettingAssertions(view.access()).assertions()
        }
    }

    fun hasLocation(location: Location) {
        val locationList = access.list ?: fail("Scene Setting List is not visible in Scene Setting tool")
        val locationItem = locationList.access().getSceneSettingItem(location.id)
            ?: fail("location item does not exist for ${location.name}")
        assertEquals(location.name.value, locationItem.text) { "Location item does not match expected name." }
    }

    fun doesNotHaveLocation(location: Location) {
        val locationList = access.list ?: return
        assertNull(
            locationList.access().getSceneSettingItem(location.id)
        ) { "Location item found for ${location.name}, but should not have." }
    }

    fun doesNotHaveLocationNamed(locationName: String) {
        val locationList = access.list ?: return
        assertNull(locationList.access().sceneSettingItems.find { it.text == locationName }) { "Location item found for ${locationName}, but should not have." }
    }

    fun hasLocationNamed(locationName: String) {
        val locationList = access.list ?: fail("Scene Setting List is not visible in Scene Setting tool")
        assertNotNull(locationList.access().sceneSettingItems.find { it.text == locationName }) { "Could not find location item for ${locationName}." }
    }

    fun locationIndicatesIssue(locationName: String) {
        val locationList = access.list ?: fail("Scene Setting List is not visible in Scene Setting tool")
        val locationItem = locationList.access().sceneSettingItems.find { it.text == locationName }
            ?: fail("Could not find location item for ${locationName}.")
        assertTrue(locationItem.hasClass(hasProblem))
    }

    fun sceneSettingItemHasReplacementOption(sceneSettingId: Location.Id, expectedReplacementOption: String) {
        val locationList = access.list ?: fail("Scene Setting List is not visible in Scene Setting tool")
        val locationItem = locationList.access().getSceneSettingItem(sceneSettingId)
            ?: fail("Scene setting item $sceneSettingId is not in list")
        assertNotNull(locationItem.access().replaceOption?.items?.find { it.text == expectedReplacementOption })
    }

    fun sceneSettingItemHasNoReplacementOptions(sceneSettingId: Location.Id) {
        val locationList = access.list ?: fail("Scene Setting List is not visible in Scene Setting tool")
        val locationItem = locationList.access().getSceneSettingItem(sceneSettingId)
            ?: fail("Scene setting item $sceneSettingId is not in list")
        locationItem.access {
            assertTrue(replaceOption!!.availableLocationItems.isEmpty())
        }
    }
}