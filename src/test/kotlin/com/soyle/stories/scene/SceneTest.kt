package com.soyle.stories.scene

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
        val characterArcSectionId = CharacterArcSection.Id(UUID.randomUUID())
        val update = Scene(Project.Id(), "", StoryEvent.Id())
            .withCharacterIncluded(character)
            .withCharacterArcSectionCovered(character.id, characterArcSectionId)
        val sections = update.getCoveredCharacterArcSectionsForCharacter(character.id)!!
        sections.single { it == characterArcSectionId }
    }

    @Test
    fun `character must already be included in scene`() {
        val character = Character.buildNewCharacter(Project.Id(), "")
        val characterArcSectionId = CharacterArcSection.Id(UUID.randomUUID())
        val scene = Scene(Project.Id(), "", StoryEvent.Id())
        val error = assertThrows<CharacterNotInScene> {
            scene.withCharacterArcSectionCovered(character.id, characterArcSectionId)
        }
        error shouldBe characterNotInScene(scene.id.uuid, character.id.uuid)
    }

    @Test
    fun `cannot cover the same character arc section twice`() {
        val character = Character.buildNewCharacter(Project.Id(), "")
        val characterArcSectionId = CharacterArcSection.Id(UUID.randomUUID())
        val scene = Scene(Project.Id(), "", StoryEvent.Id())
            .withCharacterIncluded(character)
            .withCharacterArcSectionCovered(character.id, characterArcSectionId)
        val error = assertThrows<SceneAlreadyCoversCharacterArcSection> {
            scene.withCharacterArcSectionCovered(character.id, characterArcSectionId)
        }
        assertEquals(scene.id.uuid, error.sceneId)
        assertEquals(character.id.uuid, error.characterId)
        assertEquals(characterArcSectionId.uuid, error.characterArcSectionId)
    }

    @Test
    fun `character not in scene has null character arc sections`()
    {
        val character = Character.buildNewCharacter(Project.Id(), "")
        val scene = Scene(Project.Id(), "", StoryEvent.Id())
        assertNull(scene.getCoveredCharacterArcSectionsForCharacter(character.id))
    }
}