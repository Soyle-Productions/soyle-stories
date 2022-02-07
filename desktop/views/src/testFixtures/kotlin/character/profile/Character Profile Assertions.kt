package com.soyle.stories.desktop.view.character.profile

import com.soyle.stories.character.profile.CharacterProfileView
import com.soyle.stories.desktop.view.character.profile.`Character Profile View Access`.Companion.access
import com.soyle.stories.domain.character.Character
import org.junit.jupiter.api.Assertions.*

class `Character Profile Assertions` private constructor(private val access: `Character Profile View Access`) {
    companion object {
        fun assertThat(view: CharacterProfileView, assertions: `Character Profile Assertions`.() -> Unit) {
            `Character Profile Assertions`(view.access()).assertions()
        }
    }

    fun isNotCreatingNameVariant()
    {
        val nameVariantFormVisibility = access.createCharacterNameVariantForm?.isVisible
        if (nameVariantFormVisibility == null) return
        assertFalse(nameVariantFormVisibility) { "Create character name variant form should not be visible" }
    }

    fun isCreatingNameVariant()
    {
        assertTrue(access.createCharacterNameVariantForm?.isVisible == true)
    }

    fun hasNameVariant(expectedVariant: String) {
        assertNotNull(access.getCharacterAltNameItem(expectedVariant))
    }

    fun isRenamingNameVariant(variant: String) {
        assertTrue(access.altNameRenameField(variant)?.isVisible == true)
    }

    fun isNotRenamingNameVariantFor(character: Character) = character.names.none {
        access.altNameRenameField(it.value)?.isVisible == true
    }
}