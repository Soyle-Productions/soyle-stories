package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Characters in Scene Unit Test` {

    private val scene = makeScene()
    private val character = makeCharacter()

    @Test
    fun `a character can be included in a scene`() {
        val update = scene.withCharacterIncluded(character)
        update as Updated
        update.event.sceneId.mustEqual(scene.id)
        update.event.characterInScene.characterId.mustEqual(character.id)
        update.event.characterInScene.characterName.mustEqual(character.name.value)
        update.scene.includesCharacter(character.id).mustEqual(true)
    }

    @Test
    fun `a character cannot be included in a scene more than once`() {
        val update = scene.withCharacterIncluded(character).scene.withCharacterIncluded(character)
        update as WithoutChange
        update.scene.includesCharacter(character.id).mustEqual(true)
    }

    @Test
    fun `a character in a scene can be renamed to match the name of the source character`() {
        val newName = nonBlankStr("New Name")
        val update = scene.withCharacterIncluded(character).scene.withCharacterRenamed(character.withName(newName))
        update as Updated
        update.event.sceneId.mustEqual(scene.id)
        update.event.renamedCharacter.characterId.mustEqual(character.id)
        update.event.renamedCharacter.characterName.mustEqual(newName.value)
        update.scene.includedCharacters.find { it.characterId == character.id }!!.characterName.mustEqual(newName.value)
        update.scene.includesCharacter(character.id).mustEqual(true)
    }

    @Test
    fun `a character in a scene renamed with the same name should emit no update`() {
        val update = scene.withCharacterIncluded(character).scene.withCharacterRenamed(character)
        update as WithoutChange
        update.scene.id.mustEqual(scene.id)
        update.scene.includedCharacters.find { it.characterId == character.id }!!.characterName.mustEqual(character.name.value)
    }

    @Test
    fun `a character not in a scene cannot be renamed in the scene`() {
        val error = assertThrows<SceneDoesNotIncludeCharacter> {
            scene.withCharacterRenamed(character)
        }
        error.sceneId.mustEqual(scene.id)
        error.characterId.mustEqual(character.id)
    }
}