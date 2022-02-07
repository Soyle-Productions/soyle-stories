package com.soyle.stories.desktop.view.scene.sceneCharacters

import com.soyle.stories.common.ViewOf
import com.soyle.stories.desktop.view.scene.sceneCharacters.`Scene Characters Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneCharacters.`Scene Characters Access`.Companion.drive
import com.soyle.stories.desktop.view.scene.sceneCharacters.list.CharacterInSceneItemViewAccess
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemViewModel
import com.soyle.stories.scene.characters.tool.SceneCharactersToolComponent
import com.soyle.stories.scene.characters.tool.SceneCharactersToolViewModel
import org.junit.jupiter.api.Assertions.*

class SceneCharactersAssertions private constructor(private val view: SceneCharactersToolComponent) {
    companion object {
        fun assertThat(view: SceneCharactersToolComponent, assertions: SceneCharactersAssertions.() -> Unit)
        {
            SceneCharactersAssertions(view).assertions()
        }
    }

    fun hasCharacter(character: Character) {
        val item = view.access().getCharacterItem(character.id)
        assertNotNull(item) { "Did not find character item listed in scene characters tool for ${character.displayName.value}" }
        assertEquals(character.displayName.value, item!!.name.text)
    }

    fun hasCharacterNamed(name: String) {
        assertNotNull(view.access().getCharacterItemByName(name))
    }

    fun doesNotHaveCharacter(character: Character) {
        with(view.access()) {
            assertNull(getCharacterItem(character.id))
        }
    }
    fun doesNotHaveCharacterNamed(characterName: String) {
        with(view.access()) {
            assertNull(getCharacterItemByName(characterName))
        }
    }

    fun hasNoListedStoryEventsToIncludeACharacter() {
        with(view.access()) {
            assertEquals(0, availableStoryEventItems.size)
        }
    }

    fun hasStoryEventToIncludeACharacter(storyEvent: StoryEvent) {
        val item = view.access().getAvailableStoryEventItem(storyEvent.id)
        assertNotNull(item)
        assertEquals(storyEvent.name.value, item!!.text)
    }

    fun hasNoCharactersToInclude() {
        val availableCharacterItems = view.access().availableCharacterItems
        assertEquals(0, availableCharacterItems.size) {
            "Available characters: ${availableCharacterItems.map { it.text }}"
        }
    }

    fun hasCharacterToInclude(character: Character) {
        val item = view.access().getAvailableCharacterItem(character)
        assertNotNull(item)
        assertEquals(character.displayName.value, item!!.text)
    }

    class IncludedCharacterAssertions private constructor(private val view: SceneCharactersToolComponent, private val item: CharacterInSceneItemViewAccess) {
        companion object {
            fun SceneCharactersAssertions.andCharacter(characterId: Character.Id, assertions: IncludedCharacterAssertions.() -> Unit) {
                IncludedCharacterAssertions(view, view.access().getCharacterItemOrError(characterId)).assertions()
            }
            fun SceneCharactersAssertions.andCharacter(name: String, assertions: IncludedCharacterAssertions.() -> Unit) {
                IncludedCharacterAssertions(view, view.access().getCharacterItemByName(name)!!).assertions()
            }
        }

        fun doesNotHaveRole(unexpectedRole: String? = null) {
            if (unexpectedRole == null) {
                assert(item.role.text.isBlank())
            } else {
                assertNotEquals(unexpectedRole, item.role)
            }
        }

        fun hasRole(expectedRole: String) {
            val characterRole = view.drive {
                item.role
            }
            assertEquals(expectedRole, characterRole.text)
        }

        fun hasWarning() {
            assertTrue(view.drive { item.warning }!!.text.isNotBlank())
        }

        fun isListingAvailableArcToCover(characterArcId: CharacterArc.Id, expectedName: String) {

        }

        fun isListingAvailableArcSectionToCover(characterArcId: CharacterArc.Id, sectionId: CharacterArcSection.Id, expectedLabel: String) {

        }
        fun isListingAvailableArcSectionToCover(sectionId: CharacterArcSection.Id) {

        }

    }

}