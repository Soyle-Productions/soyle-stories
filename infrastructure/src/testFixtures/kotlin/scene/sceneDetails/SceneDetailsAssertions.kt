package com.soyle.stories.desktop.view.scene.sceneDetails

import com.soyle.stories.scene.sceneDetails.SceneDetails
import org.junit.jupiter.api.Assertions.assertEquals

class SceneDetailsAssertions private constructor(private val driver: SceneDetailsDriver) {
    companion object {
        fun assertThat(sceneDetails: SceneDetails, assertions: SceneDetailsAssertions.() -> Unit)
        {
            SceneDetailsAssertions(SceneDetailsDriver(sceneDetails)).assertions()
        }
    }

    inner class IncludedCharacterAssertions(private val driver: SceneDetailsDriver.IncludedCharacterDriver) {
        fun hasInheritedMotivationValue(motivation: String)
        {
            assertEquals(motivation, driver.getMotivationFieldInput().promptText)
        }
        fun hasMotivationValue(motivation: String)
        {
            assertEquals(motivation, driver.getMotivationFieldInput().text)
        }
    }

    fun andCharacter(characterId: String, assertions: IncludedCharacterAssertions.() -> Unit) {
        IncludedCharacterAssertions(driver.getIncludedCharacter(characterId)).assertions()
    }

}