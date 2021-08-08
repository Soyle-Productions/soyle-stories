package com.soyle.stories.desktop.view.theme.characterConflict

import com.soyle.stories.desktop.view.theme.characterConflict.`Character Conflict View Access`.Companion.access
import com.soyle.stories.theme.characterConflict.CharacterConflict
import javafx.scene.control.TextInputControl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.fail

class `Character Conflict Assertions` private constructor(val access: `Character Conflict View Access`) {
    companion object {
        fun CharacterConflict.assertThat(assertions: `Character Conflict Assertions`.() -> Unit) {
            `Character Conflict Assertions`(access()).assertions()
        }
    }

    fun psychologicalWeaknessHasValue(expectedValue: String)
    {
        val input = access.psychologicalWeaknessInput ?: fail("psychological weakness input doesn't exist")
        assertEquals(expectedValue, input.text) { "Wrong value for psychological weakness found" }
    }

    fun moralWeaknessHasValue(expectedValue: String)
    {
        val input = access.moralWeaknessInput ?: fail("moral weakness input doesn't exist")
        assertEquals(expectedValue, input.text) { "Wrong value for moral weakness found" }
    }

    fun TextInputControl?.hasValue(expectedValue: String)
    {
        if (this == null) fail("input doesn't exist")
        assertEquals(expectedValue, text) { "Wrong value for input found" }
    }
}