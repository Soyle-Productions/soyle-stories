package com.soyle.stories.desktop.view.scene.sceneCharacters

import com.soyle.stories.common.exists
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.scene.sceneCharacters.SceneCharactersView
import com.soyle.stories.scene.sceneCharacters.includedCharacterItem.IncludedCharacterItemView
import org.junit.jupiter.api.Assertions.*

class SceneCharactersAssertions private constructor(private val view: SceneCharactersView) {
    companion object {
        fun assertThat(view: SceneCharactersView, assertions: SceneCharactersAssertions.() -> Unit)
        {
            SceneCharactersAssertions(view).assertions()
        }
    }

    fun hasCharacter(character: Character) {
        with(view.driver()) {
            assertNotNull(getCharacterItem(character.id))
        }
    }

    fun doesNotHaveCharacter(character: Character) {
        with(view.driver()) {
            assertNull(getCharacterItem(character.id))
        }
    }
    fun doesNotHaveCharacterNamed(characterName: String) {
        with(view.driver()) {
            assertNull(getCharacterItemByName(characterName))
        }
    }

    class IncludedCharacterAssertions private constructor(private val view: SceneCharactersView, private val item: IncludedCharacterItemView) {
        companion object {
            fun SceneCharactersAssertions.andCharacter(characterId: Character.Id, assertions: IncludedCharacterAssertions.() -> Unit) {
                IncludedCharacterAssertions(view, view.driver().getCharacterItemOrError(characterId)).assertions()
            }
        }

        fun doesNotHaveDesire() {
            val desireInput = view.drive {
                getCharacterEditorOrError().desireInput
            }
            assert(desireInput.text.isNullOrEmpty())
        }

        fun hasDesire(expectedDesire: String) {
            val desireInput = view.drive {
                getCharacterEditorOrError().desireInput
            }
            assertEquals(expectedDesire, desireInput.text ?: "")
        }

        fun hasMotivationValue(expectedMotivation: String) {
            val motivationInput = view.drive {
                getCharacterEditorOrError().motivationInput
            }
            assertEquals(expectedMotivation, motivationInput.text ?: "")
        }

        fun hasInheritedMotivationValue(expectedInheritedMotivation: String) {
            val motivationInput = view.drive {
                getCharacterEditorOrError().motivationInput
            }
            assertEquals(expectedInheritedMotivation, motivationInput.promptText)
        }

        fun doesNotHaveRole(unexpectedRole: String? = null) {
            val characterRole = view.drive {
                item.role
            }
            if (unexpectedRole == null) {
                assert(characterRole.text.isNullOrBlank())
                assertFalse(characterRole.exists)
            } else {
                assertNotEquals(unexpectedRole, characterRole.text)
            }
        }

        fun hasRole(expectedRole: String) {
            val characterRole = view.drive {
                item.role
            }
            assertEquals(expectedRole, characterRole.text)
            assertTrue(characterRole.exists)
        }

        fun isListingAvailableArcToCover(characterArcId: CharacterArc.Id, expectedName: String) {

        }

        fun isListingAvailableArcSectionToCover(characterArcId: CharacterArc.Id, sectionId: CharacterArcSection.Id, expectedLabel: String) {

        }
        fun isListingAvailableArcSectionToCover(sectionId: CharacterArcSection.Id) {

        }

    }

}