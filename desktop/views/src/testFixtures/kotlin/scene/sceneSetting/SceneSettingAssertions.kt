package com.soyle.stories.desktop.view.scene.sceneSetting

import com.soyle.stories.domain.location.Location
import com.soyle.stories.scene.sceneSetting.SceneSettingView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.fail

class SceneSettingAssertions private constructor(private val driver: `Scene Setting Driver`) {
    companion object {
        fun assertThat(view: SceneSettingView, assertions: SceneSettingAssertions.() -> Unit) {
            SceneSettingAssertions(view.driver()).assertions()
        }
    }

    fun hasLocation(location: Location)
    {
        val locationItem = driver.getLocationItem(location.id) ?: fail("location item does not exist for ${location.name}")
        assertEquals(location.name.value, locationItem.text) { "Location item does not match expected name." }
    }

    fun doesNotHaveLocation(location: Location)
    {
        assertNull(driver.getLocationItem(location.id)) { "Location item found for ${location.name}, but should not have." }
    }
}