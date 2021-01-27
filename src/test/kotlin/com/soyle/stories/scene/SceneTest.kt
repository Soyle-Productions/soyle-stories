package com.soyle.stories.scene

import com.soyle.stories.character.characterName
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.character.makeCharacterArcSection
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.entities.*
import com.soyle.stories.theme.makeSymbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SceneTest {

    @Test
    fun `scene includes character`() {
        val character = makeCharacter()
        val scene = makeScene()
        assert(
            scene.withCharacterIncluded(character).includesCharacter(character.id)
        )
    }

    @Test
    fun `Scene cannot include character twice`() {
        val character = makeCharacter()
        val scene = makeScene().withCharacterIncluded(character)
        assertThrows<SceneAlreadyContainsCharacter> {
            scene.withCharacterIncluded(character)
        } shouldBe sceneAlreadyContainsCharacter(scene.id.uuid, character.id.uuid)
    }

    @Test
    fun `scene covers character arc section`() {
        val character = Character.buildNewCharacter(Project.Id(), characterName())
        val characterArcSection = makeCharacterArcSection(characterId = character.id)
        val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
            .withCharacterIncluded(character)
            .withCharacterArcSectionCovered(characterArcSection)
        val sections = update.getCoveredCharacterArcSectionsForCharacter(character.id)!!
        sections.single { it == characterArcSection.id }
    }

    @Test
    fun `character must already be included in scene`() {
        val character = Character.buildNewCharacter(Project.Id(), characterName())
        val characterArcSection = makeCharacterArcSection(characterId = character.id)
        val scene = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
        val error = assertThrows<CharacterNotInScene> {
            scene.withCharacterArcSectionCovered(characterArcSection)
        }
        error shouldBe characterNotInScene(scene.id.uuid, character.id.uuid)
    }

    @Test
    fun `cannot cover the same character arc section twice`() {
        val character = Character.buildNewCharacter(Project.Id(), characterName())
        val characterArcSection = makeCharacterArcSection(characterId = character.id)
        val scene = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
            .withCharacterIncluded(character)
            .withCharacterArcSectionCovered(characterArcSection)
        val error = assertThrows<SceneAlreadyCoversCharacterArcSection> {
            scene.withCharacterArcSectionCovered(characterArcSection)
        }
        assertEquals(scene.id.uuid, error.sceneId)
        assertEquals(character.id.uuid, error.characterId)
        assertEquals(characterArcSection.id.uuid, error.characterArcSectionId)
    }

    @Test
    fun `character not in scene has null character arc sections`() {
        val character = Character.buildNewCharacter(Project.Id(), characterName())
        val scene = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
        assertNull(scene.getCoveredCharacterArcSectionsForCharacter(character.id))
    }

    @Nested
    inner class `Track Symbols in Scene` {

        @Test
        fun `can track a symbol in a scene`() {
            val symbol = makeSymbol()
            val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(symbol)
            update.scene.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(true) { "Did not track symbol $symbol" }
            update as Single
            update.event as SymbolTrackedInScene
        }

        @Test
        fun `can stop tracking a symbol in a scene`() {
            val symbol = makeSymbol()
            val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(symbol).scene
                .withoutSymbolTracked(symbol.id)
            update.scene.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(false) { "Did not stop tracking symbol"}
            update as Single
            update.event as TrackedSymbolRemoved
        }

        @Test
        fun `can list all tracked symbols`() {
            val symbols = List(3) { makeSymbol() }
            val scene: Scene = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(symbols[0]).scene
                .withSymbolTracked(symbols[1]).scene
                .withSymbolTracked(symbols[2]).scene
            scene.trackedSymbols.size.mustEqual(3)
            scene.trackedSymbols.forEachIndexed { index, trackedSymbol ->
                trackedSymbol.symbolId.mustEqual(symbols[index].id)
                trackedSymbol.symbolName.mustEqual(symbols[index].name)
            }
        }

        @Test
        fun `cannot add symbol more than once`() {
            val symbol = makeSymbol()
            val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(symbol).scene
                .withSymbolTracked(symbol)
            update.scene.trackedSymbols.size.mustEqual(1)
            update as NoUpdate
        }

        @Test
        fun `cannot create scene with same symbol more than once`() {
            val symbol = makeSymbol()
            assertThrows<IllegalStateException> {
                Scene(Scene.Id(), Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), setOf(), Prose.Id(), listOf(), symbols = List(2) {
                    Scene.TrackedSymbol(symbol.id, symbol.name + it)
                })
            }
        }

        @Test
        fun `adding same symbol with new name should update name`() {
            val symbol = makeSymbol()
            val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(symbol).scene
                .withSymbolTracked(symbol.withName("New Symbol Name"))
            update.scene.trackedSymbols.size.mustEqual(1)
            update.scene.trackedSymbols.single().symbolName.mustEqual("New Symbol Name")
            update as Single
            with (update.event as TrackedSymbolRenamed) {
                trackedSymbol.symbolName.mustEqual("New Symbol Name")
            }
        }

    }
}