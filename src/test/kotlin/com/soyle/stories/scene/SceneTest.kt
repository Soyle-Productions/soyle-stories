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
import com.soyle.stories.theme.makeTheme
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

        private val symbol = makeSymbol()
        private val theme = makeTheme(symbols = listOf(symbol))

        @Test
        fun `symbol must be in theme`() {
            val failureTheme = makeTheme()
            val scene = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
            val error = assertThrows<IllegalArgumentException> {
                scene.withSymbolTracked(failureTheme, symbol)
            }
            error.message.mustEqual("Symbol ${symbol.name} is not contained within the ${failureTheme.name} theme")
        }

        @Test
        fun `can track a symbol in a scene`() {
            val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(theme, symbol)
            update.scene.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(true) { "Did not track symbol $symbol" }
            update.scene.trackedSymbols.single().isPinned.mustEqual(false)
            update as Updated
            update.event as SymbolTrackedInScene
        }

        @Test
        fun `cannot add symbol more than once`() {
            val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(theme, symbol).scene
                .withSymbolTracked(theme, symbol)
            update.scene.trackedSymbols.size.mustEqual(1)
            update as NoUpdate
        }

        @Test
        fun `cannot create scene with same symbol more than once`() {
            assertThrows<IllegalStateException> {
                Scene(Scene.Id(), Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), setOf(), Prose.Id(), listOf(), symbols = List(2) {
                    Scene.TrackedSymbol(symbol.id, symbol.name + it, theme.id)
                })
            }
        }

        @Nested
        inner class `Pin symbol to scene`
        {

            @Test
            fun `can manually track a symbol in a scene`() {
                val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                    .withSymbolTracked(theme, symbol, true)
                update.scene.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(true) { "Did not track symbol $symbol" }
                update.scene.trackedSymbols.single().isPinned.mustEqual(true)
                update as Updated
                update.event as SymbolTrackedInScene
            }

            @Test
            fun `symbol must be tracked in scene to pin`() {
                val scene = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                assertThrows<SceneDoesNotTrackSymbol> {
                    scene.withSymbolPinned(symbol.id)
                }
            }

            @Test
            fun `can update a tracked symbol to pin the symbol`() {
                val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                    .withSymbolTracked(theme, symbol).scene
                    .withSymbolPinned(symbol.id)
                update.scene.trackedSymbols.single().isPinned.mustEqual(true)
                update as Updated
            }

            @Test
            fun `no update if already pinned`() {
                val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                    .withSymbolTracked(theme, symbol, true).scene
                    .withSymbolPinned(symbol.id)
                update.scene.trackedSymbols.single().isPinned.mustEqual(true)
                update as NoUpdate
            }

            @Test
            fun `symbol must be tracked in scene to unpin`() {
                val scene = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                assertThrows<SceneDoesNotTrackSymbol> {
                    scene.withSymbolUnpinned(symbol.id)
                }
            }

            @Test
            fun `can update a tracked symbol to unpin the symbol`() {
                val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                    .withSymbolTracked(theme, symbol, true).scene
                    .withSymbolUnpinned(symbol.id)
                update.scene.trackedSymbols.single().isPinned.mustEqual(false)
                update as Updated
            }

            @Test
            fun `no update if already unpinned`() {
                val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                    .withSymbolTracked(theme, symbol).scene
                    .withSymbolUnpinned(symbol.id)
                update.scene.trackedSymbols.single().isPinned.mustEqual(false)
                update as NoUpdate
            }

        }

        @Test
        fun `symbol must be tracked in scene to rename`() {
            val scene = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
            assertThrows<SceneDoesNotTrackSymbol> {
                scene.withSymbolRenamed(symbol.id, "whatever")
            }
        }

        @Test
        fun `can update tracked symbol name`() {
            val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(theme, symbol).scene
                .withSymbolRenamed(symbol.id, "New Symbol Name")
            update.scene.trackedSymbols.size.mustEqual(1)
            update.scene.trackedSymbols.single().symbolName.mustEqual("New Symbol Name")
            update as Updated
            with (update.event) {
                trackedSymbol.symbolName.mustEqual("New Symbol Name")
            }
        }

        @Test
        fun `no update if name is equal`() {
            val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(theme, symbol).scene
                .withSymbolRenamed(symbol.id, symbol.name)
            update.scene.trackedSymbols.single().symbolName.mustEqual(symbol.name)
            update as NoUpdate
        }

        @Test
        fun `can stop tracking a symbol in a scene`() {
            val update = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(theme, symbol).scene
                .withoutSymbolTracked(symbol.id)
            update.scene.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(false) { "Did not stop tracking symbol"}
            update as Updated
            update.event as TrackedSymbolRemoved
        }

        @Test
        fun `can list all tracked symbols`() {
            val symbols = List(3) { makeSymbol() }
            val theme = makeTheme(symbols = symbols)
            val scene: Scene = Scene(Project.Id(), NonBlankString.create(str())!!, StoryEvent.Id(), Prose.Id())
                .withSymbolTracked(theme, symbols[0]).scene
                .withSymbolTracked(theme, symbols[1]).scene
                .withSymbolTracked(theme, symbols[2]).scene
            scene.trackedSymbols.size.mustEqual(3)
            scene.trackedSymbols.forEachIndexed { index, trackedSymbol ->
                trackedSymbol.symbolId.mustEqual(symbols[index].id)
                trackedSymbol.symbolName.mustEqual(symbols[index].name)
            }
        }

    }
}