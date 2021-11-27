package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.character.makeCharacterArcSection
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SceneTest {

    @Nested
    inner class `When Scene is Created` {

        val projectId = Project.Id()
        val proseId = Prose.Id()
        val storyEventId = StoryEvent.Id()
        val inputName = sceneName()

        @Test
        fun `should produce new scene with provided inputs`() {
            val update: SceneUpdate<*> = Scene.create(projectId, inputName, storyEventId, proseId)

            update.scene.name.mustEqual(inputName)
            update.scene.projectId.mustEqual(projectId)
            update.scene.coveredStoryEvents.single().mustEqual(storyEventId)
            update.scene.proseId.mustEqual(proseId)
        }

        @Test
        fun `should produce scene created event`() {
            val update: SceneUpdate<SceneCreated> = Scene.create(projectId, inputName, storyEventId, proseId)

            update as Updated
            update.event.sceneId.mustEqual(update.scene.id)
            update.event.name.mustEqual(update.scene.name)
            update.event.proseId.mustEqual(update.scene.proseId)
            update.event.storyEventId.mustEqual(update.scene.coveredStoryEvents.single())
        }

    }

    @Nested
    inner class `With Scene Renamed` {

        private val scene = makeScene()
        private val newName = sceneName()

        @Test
        fun `should update scene`() {
            val (newScene, _) = scene.withName(newName) as Updated
            newScene.name.mustEqual(newName)
        }

        @Test
        fun `should produce scene renamed event`() {
            val (_, event) = scene.withName(newName) as Updated
            event.mustEqual(SceneRenamed(scene.id, newName.value))
        }

        @Nested
        inner class `When Same Name Provided` {

            @Test
            fun `should not produce event`() {
                scene.withName(scene.name) as WithoutChange
            }

        }
    }

    @Test
    fun `scene covers character arc section`() {
        val character = makeCharacter()
        val characterArcSection = makeCharacterArcSection(characterId = character.id)
        val update = makeScene()
            .withCharacterIncluded(character).scene
            .withCharacterArcSectionCovered(characterArcSection)
        val sections = update.getCoveredCharacterArcSectionsForCharacter(character.id)!!
        sections.single { it == characterArcSection.id }
    }

    @Test
    fun `character must already be included in scene`() {
        val character = makeCharacter()
        val characterArcSection = makeCharacterArcSection(characterId = character.id)
        val scene = makeScene()
        val error = assertThrows<SceneDoesNotIncludeCharacter> {
            scene.withCharacterArcSectionCovered(characterArcSection)
        }
        error.sceneId.mustEqual(scene.id)
        error.characterId.mustEqual(character.id)
    }

    @Test
    fun `cannot cover the same character arc section twice`() {
        val character = makeCharacter()
        val characterArcSection = makeCharacterArcSection(characterId = character.id)
        val scene = makeScene()
            .withCharacterIncluded(character).scene
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
        val character = makeCharacter()
        val scene = makeScene()
        assertNull(scene.getCoveredCharacterArcSectionsForCharacter(character.id))
    }

    @Nested
    inner class `Track Symbols in Scene` {

        private val symbol = makeSymbol()
        private val theme = makeTheme(symbols = listOf(symbol))

        @Test
        fun `symbol must be in theme`() {
            val failureTheme = makeTheme()
            val scene = makeScene()
            val error = assertThrows<IllegalArgumentException> {
                scene.withSymbolTracked(failureTheme, symbol)
            }
            error.message.mustEqual("Symbol ${symbol.name} is not contained within the ${failureTheme.name} theme")
        }

        @Test
        fun `can track a symbol in a scene`() {
            val update = makeScene()
                .withSymbolTracked(theme, symbol)
            update.scene.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(true) { "Did not track symbol $symbol" }
            update.scene.trackedSymbols.single().isPinned.mustEqual(false)
            update as Updated
        }

        @Test
        fun `cannot add symbol more than once`() {
            val update = makeScene()
                .withSymbolTracked(theme, symbol).scene
                .withSymbolTracked(theme, symbol)
            update.scene.trackedSymbols.size.mustEqual(1)
            update as WithoutChange
        }

        @Test
        fun `cannot create scene with same symbol more than once`() {
            assertThrows<IllegalStateException> {
                makeScene(symbols = List(2) {
                    Scene.TrackedSymbol(symbol.id, symbol.name + it, theme.id)
                })
            }
        }

        @Nested
        inner class `Pin symbol to scene`
        {

            @Test
            fun `can manually track a symbol in a scene`() {
                val update = makeScene()
                    .withSymbolTracked(theme, symbol, true)
                update.scene.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(true) { "Did not track symbol $symbol" }
                update.scene.trackedSymbols.single().isPinned.mustEqual(true)
                update as Updated
            }

            @Test
            fun `symbol must be tracked in scene to pin`() {
                val scene = makeScene()
                assertThrows<SceneDoesNotTrackSymbol> {
                    scene.withSymbolPinned(symbol.id)
                }
            }

            @Test
            fun `can update a tracked symbol to pin the symbol`() {
                val update = makeScene()
                    .withSymbolTracked(theme, symbol).scene
                    .withSymbolPinned(symbol.id)
                update.scene.trackedSymbols.single().isPinned.mustEqual(true)
                update as Updated
            }

            @Test
            fun `no update if already pinned`() {
                val update = makeScene()
                    .withSymbolTracked(theme, symbol, true).scene
                    .withSymbolPinned(symbol.id)
                update.scene.trackedSymbols.single().isPinned.mustEqual(true)
                update as WithoutChange
            }

            @Test
            fun `symbol must be tracked in scene to unpin`() {
                val scene = makeScene()
                assertThrows<SceneDoesNotTrackSymbol> {
                    scene.withSymbolUnpinned(symbol.id)
                }
            }

            @Test
            fun `can update a tracked symbol to unpin the symbol`() {
                val update = makeScene()
                    .withSymbolTracked(theme, symbol, true).scene
                    .withSymbolUnpinned(symbol.id)
                update.scene.trackedSymbols.single().isPinned.mustEqual(false)
                update as Updated
            }

            @Test
            fun `no update if already unpinned`() {
                val update = makeScene()
                    .withSymbolTracked(theme, symbol).scene
                    .withSymbolUnpinned(symbol.id)
                update.scene.trackedSymbols.single().isPinned.mustEqual(false)
                update as WithoutChange
            }

        }

        @Test
        fun `symbol must be tracked in scene to rename`() {
            val scene = makeScene()
            assertThrows<SceneDoesNotTrackSymbol> {
                scene.withSymbolRenamed(symbol.id, "whatever")
            }
        }

        @Test
        fun `can update tracked symbol name`() {
            val update = makeScene()
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
            val update = makeScene()
                .withSymbolTracked(theme, symbol).scene
                .withSymbolRenamed(symbol.id, symbol.name)
            update.scene.trackedSymbols.single().symbolName.mustEqual(symbol.name)
            update as WithoutChange
        }

        @Test
        fun `can stop tracking a symbol in a scene`() {
            val update = makeScene()
                .withSymbolTracked(theme, symbol).scene
                .withoutSymbolTracked(symbol.id)
            update.scene.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(false) { "Did not stop tracking symbol"}
            update as Updated
        }

        @Test
        fun `can list all tracked symbols`() {
            val symbols = List(3) { makeSymbol() }
            val theme = makeTheme(symbols = symbols)
            val scene: Scene = makeScene()
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
