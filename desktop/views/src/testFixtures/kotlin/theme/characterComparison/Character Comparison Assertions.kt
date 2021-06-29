package com.soyle.stories.desktop.view.theme.characterComparison

import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.desktop.view.theme.characterComparison.`Character Card View Access`.Companion.access
import com.soyle.stories.desktop.view.theme.characterComparison.`Character Comparison View Access`.Companion.access
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparison
import javafx.scene.Node
import org.junit.jupiter.api.Assertions.*

class `Character Comparison Assertions` private constructor(val access: `Character Comparison View Access`) {
    companion object {

        fun CharacterValueComparison.assertThat(assertions: `Character Comparison Assertions`.() -> Unit) =
            `Character Comparison Assertions`(access()).assertions()
    }

    fun hasIncludedCharacter(characterId: Character.Id) {
        assertNotNull(access.getCharacterCard(characterId)) { "Character Value Comparison does not include $characterId" }
    }

    class `Included Character Assertions` private constructor(
        val access: `Character Comparison View Access`,
        private val cardAccess: `Character Card View Access`
    ) {

        companion object {

            fun `Character Comparison Assertions`.includedCharacter(characterId: Character.Id): `Included Character Assertions`? =
                access.getCharacterCard(characterId)?.let { `Included Character Assertions`(access, it.access()) }
        }

        fun hasNoValues() {
            assertTrue(cardAccess.values.isEmpty())
        }

        fun hasValue(valueWeb: ValueWeb, oppositionValue: OppositionValue)
        {
            val value = cardAccess.getValue(oppositionValue.id) ?: fail("Character does not have value for ${oppositionValue.name}")
            assertEquals("(${valueWeb.name}) ${oppositionValue.name}", value.text)
        }

        fun doesNotHaveValue(valueWebName: String, oppositionName: String)
        {
            assertNull(cardAccess.values.find { it.text == "{$valueWebName} $oppositionName" })
        }

    }

}