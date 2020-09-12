package com.soyle.stories.scene

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.character.makeCharacterArcSection
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class SceneTest {

    @Test
    fun `scene covers character arc section`() {
        val character = Character.buildNewCharacter(Project.Id(), "")
        val characterArcSection = makeCharacterArcSection(characterId = character.id)
        val update = Scene(Project.Id(), "", StoryEvent.Id())
            .withCharacterIncluded(character)
            .withCharacterArcSectionCovered(character.id, characterArcSection)
        val sections = update.getCoveredCharacterArcSectionsForCharacter(character.id)!!
        sections.single { it == characterArcSection.id }
    }

    @Test
    fun `character must already be included in scene`() {
        val character = Character.buildNewCharacter(Project.Id(), "")
        val characterArcSection = makeCharacterArcSection(characterId = character.id)
        val scene = Scene(Project.Id(), "", StoryEvent.Id())
        val error = assertThrows<CharacterNotInScene> {
            scene.withCharacterArcSectionCovered(character.id, characterArcSection)
        }
        error shouldBe characterNotInScene(scene.id.uuid, character.id.uuid)
    }

    @Test
    fun `cannot cover the same character arc section twice`() {
        val character = Character.buildNewCharacter(Project.Id(), "")
        val characterArcSection = makeCharacterArcSection(characterId = character.id)
        val scene = Scene(Project.Id(), "", StoryEvent.Id())
            .withCharacterIncluded(character)
            .withCharacterArcSectionCovered(character.id, characterArcSection)
        val error = assertThrows<SceneAlreadyCoversCharacterArcSection> {
            scene.withCharacterArcSectionCovered(character.id, characterArcSection)
        }
        assertEquals(scene.id.uuid, error.sceneId)
        assertEquals(character.id.uuid, error.characterId)
        assertEquals(characterArcSection.id.uuid, error.characterArcSectionId)
    }

    @Test
    fun `character not in scene has null character arc sections`()
    {
        val character = Character.buildNewCharacter(Project.Id(), "")
        val scene = Scene(Project.Id(), "", StoryEvent.Id())
        assertNull(scene.getCoveredCharacterArcSectionsForCharacter(character.id))
    }

    @Test
    fun `covered character arc section must belong to associated character`() {
        val owner = makeCharacter()
        val requestedCharacter = makeCharacter()
        val scene = listOf(owner, requestedCharacter).fold(Scene(Project.Id(), "", StoryEvent.Id())) { scene, character ->
            scene.withCharacterIncluded(character)
        }
        val characterArcSection = makeCharacterArcSection(characterId = owner.id)
        assertThrows<CharacterArcSectionIsNotPartOfCharactersArc> {
            scene.withCharacterArcSectionCovered(requestedCharacter.id, characterArcSection)
        }.run {
            assertEquals(requestedCharacter.id.uuid, characterId)
            assertEquals(characterArcSection.id.uuid, characterArcSectionId)
            assertEquals(owner.id.uuid, expectedCharacterId)
        }
    }
}