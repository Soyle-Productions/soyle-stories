package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.singleLine
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.renameSymbol.RenameSymbol
import com.soyle.stories.usecase.theme.renameSymbol.RenameSymbolUseCase
import com.soyle.stories.usecase.theme.renameSymbol.RenamedSymbol
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RenameSymbolUnitTest {

    private val themeId = Theme.Id()
    private val symbol = makeSymbol()

    private var result: RenameSymbol.ResponseModel? = null

    private var updatedTheme: Theme? = null
    private var updatedScenes: MutableList<Scene> = mutableListOf()
    private var updatedProse: Prose? = null

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = updatedScenes::add)
    private val proseRepository = ProseRepositoryDouble(onReplaceProse = ::updatedProse::set)

    @Test
    fun `symbol does not exist`() {
        val error = assertThrows<SymbolDoesNotExist> {
            renameSymbol()
        }
        assertNull(updatedTheme)
        error shouldBe symbolDoesNotExist(symbol.id.uuid)
    }

    @Test
    fun `symbol has same name`() {
        val sameName = nonBlankStr("Pre-existing name")
        themeRepository.themes[themeId] = makeTheme(themeId, symbols = listOf(
            symbol.withName(sameName.value)
        ))
        val error = assertThrows<SymbolAlreadyHasName> {
            renameSymbol(sameName)
        }
        assertNull(updatedTheme)
        error shouldBe symbolAlreadyHasName(symbol.id.uuid, sameName.value)
    }

    @Test
    fun `input name is valid and different`() {
        val newName = nonBlankStr("Valid New Name")
        val theme = makeTheme(themeId, symbols = listOf(
            Symbol(symbol.id, "Different Name")
        ))
        themeRepository.themes[themeId] = theme
        renameSymbol(newName)
        assertNotNull(updatedTheme)
        assertEquals(theme.withoutSymbol(symbol.id).withSymbol(Symbol(symbol.id, newName.value)), updatedTheme!!)
        result!!.renamedSymbol shouldBe renamedSymbol(newName.value)
    }

    @Nested
    inner class `Rule - All Prose that mention the symbol should update the mention of that symbol` {

        private val prose = makeProse(
            content = listOf(
                ProseContent("", symbol.id.mentioned(themeId) to singleLine(symbol.name))
            )
        )

        init {
            themeRepository.givenTheme(makeTheme(themeId, symbols = listOf(symbol)))
            proseRepository.givenProse(prose)
        }

        @Test
        fun `should update prose`() {
            val newName = nonBlankStr("Valid New Name")
            renameSymbol(newName)
            updatedProse!!.let {
                it.text.mustEqual(newName) { "prose with only mention should have entire content replaced" }
                it.mentions.single().run {
                    entityId.mustEqual(symbol.id.mentioned(themeId))
                    startIndex.mustEqual(0)
                    endIndex.mustEqual("Valid New Name".length)
                }
            }
        }

        @Test
        fun `should output prose mention text replaced events`() {
            val newName = nonBlankStr()
            renameSymbol(newName)
            result!!.mentionTextReplaced.single().let {
                it.deletedText.mustEqual(symbol.name)
                it.entityId.mustEqual(symbol.id.mentioned(themeId))
                it.insertedText.mustEqual(newName)
                it.newContent.mustEqual(updatedProse!!.text)
                it.newMentions.mustEqual(updatedProse!!.mentions)
            }
        }

    }

    @Nested
    inner class `Symbol tracked in scenes`
    {

        private val scenesWithSymbol = List(5) {
            makeScene(symbols = listOf(Scene.TrackedSymbol(symbol.id, symbol.name, themeId)))
        }
        private val scenesWithoutSymbol = List(4) {
            makeScene()
        }

        init {
            themeRepository.givenTheme(makeTheme(themeId, symbols = listOf(symbol)))
            scenesWithSymbol.forEach(sceneRepository::givenScene)
            scenesWithoutSymbol.forEach(sceneRepository::givenScene)
        }

        @Test
        fun `should update scenes with symbol`() {
            val newName = nonBlankStr()
            renameSymbol(newName)
            updatedScenes.size.mustEqual(scenesWithSymbol.size)
            updatedScenes.map { it.id }.toSet().mustEqual(scenesWithSymbol.map { it.id }.toSet())
            updatedScenes.forEach { it.trackedSymbols.getSymbolById(symbol.id)!!.symbolName.mustEqual(newName) }
        }

        @Test
        fun `should output renamed tracked symbol events`() {
            renameSymbol()
            result!!.trackedSymbolsRenamed.let {
                it.size.mustEqual(scenesWithSymbol.size)
                it.map { it.sceneId }.toSet().mustEqual(scenesWithSymbol.map { it.id }.toSet())
                it.forEach { it.trackedSymbol.symbolId.mustEqual(symbol.id) }
            }
        }
    }

    private fun renameSymbol(name: NonBlankString = nonBlankStr()) {
        val useCase: RenameSymbol = RenameSymbolUseCase(themeRepository, sceneRepository, proseRepository)
        val output = object : RenameSymbol.OutputPort {
            override suspend fun symbolRenamed(response: RenameSymbol.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(symbol.id.uuid, name, output)
        }
    }

    private fun renamedSymbol(expectedName: String): (Any?) -> Unit = { actual ->
        actual as RenamedSymbol
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(symbol.id.uuid, actual.symbolId)
        assertEquals(expectedName, actual.newName)
    }

}