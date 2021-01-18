package com.soyle.stories.desktop.view.scene.sceneDetails

import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.scene.sceneDetails.SceneDetails
import javafx.scene.control.CheckBox
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

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

        fun isListingAvailableArcToCover(arcId: CharacterArc.Id, expectedName: String)
        {
            assertTrue(driver.getPositionOnArcInput().isShowing) { "Not currently listing available positions on arc." }
            val arcItem = driver.getPositionOnArcInput().items.find { it.id == arcId.uuid.toString() }
                ?: error("Could not find listed available arc to cover with id of $arcId")
            assertEquals(expectedName, arcItem.text)
        }

        fun isListingAvailableArcSectionToCover(arcId: CharacterArc.Id? = null, sectionId: CharacterArcSection.Id, expectedName: String? = null)
        {
            assertTrue(driver.getPositionOnArcInput().isShowing) { "Not currently listing available positions on arc." }
            val sectionItem: MenuItem
            if (arcId != null) {
                val arcItem = driver.getPositionOnArcInput().items.find { it.id == arcId.uuid.toString() }
                    ?: error("Could not find listed available arc to cover with id of $arcId")
                arcItem as Menu
                sectionItem = arcItem.items.find { it.id == sectionId.uuid.toString() }
                    ?: error("Could not find listed available arc section to cover with id of $sectionId")
            } else {
                sectionItem = driver.getPositionOnArcInput().items.flatMap { (it as? Menu)?.items ?: emptyList() }
                    .find { it.id == sectionId.uuid.toString() }
                    ?: error("Could not find listed available arc section to cover with id of $sectionId")
            }
            sectionItem as CustomMenuItem
            val content = sectionItem.content as CheckBox
            if (expectedName != null) assertEquals(expectedName, content.text)
        }
    }

    fun andCharacter(characterId: String, assertions: IncludedCharacterAssertions.() -> Unit) {
        IncludedCharacterAssertions(driver.getIncludedCharacter(characterId)).assertions()
    }

}