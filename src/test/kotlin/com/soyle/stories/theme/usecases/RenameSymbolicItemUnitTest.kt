package com.soyle.stories.theme.usecases

import arrow.core.extensions.list.align.empty
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeOppositionValue
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.makeValueWeb
import com.soyle.stories.theme.usecases.renameSymbolicItems.RenameSymbolicItem
import com.soyle.stories.theme.usecases.renameSymbolicItems.RenameSymbolicItemUseCase
import com.soyle.stories.theme.usecases.renameSymbolicItems.RenamedSymbolicItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.math.exp

class RenameSymbolicItemUnitTest {

    private val symbolicEntityId = UUID.randomUUID()
    private val originalName = "OG Name ${UUID.randomUUID()}"
    private val newName = "New Name ${UUID.randomUUID()}"

    private var updatedThemes: List<Theme>? = null
    private var result: Any? = null

    @Nested
    inner class `No themes have the entity as a symbolic item` {

        @AfterEach
        fun `assert empty`() {
            renameSymbolicItem()
            assertNull(updatedThemes)
            result shouldBe ::empty
        }

        @Test
        fun `no themes exist`() {}

        @Test
        fun `themes exist without entity`() {
            givenThemes()
        }

    }

    @Test
    fun `one opposition value has entity`() {
        givenOppositionWithEntity()
        renameSymbolicItem()
        updatedThemes shouldBe listOfThemesOfSize(1)
        result shouldBe responseModel(1)
    }

    @Test
    fun `all oppositions per value web per theme should be output`() {
        givenOppositionWithEntity(themeCount = 3, valueWebCount = 4, oppositionCount = 5)
        renameSymbolicItem()
        updatedThemes shouldBe listOfThemesOfSize(3)
        result shouldBe responseModel(60)
    }

    @Test
    fun `only oppositions with entity output`() {
        givenOppositionWithEntity(themeCount = 2, valueWebCount = 3, oppositionCount = 4)
        givenThemes(themeCount = 3, valueWebCount = 4, oppositionCount = 5)
        renameSymbolicItem()
        updatedThemes shouldBe listOfThemesOfSize(2)
        result shouldBe responseModel(24)
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedThemes = (updatedThemes ?: listOf()) + it
    })

    private fun givenOppositionWithEntity(themeCount: Int = 1, valueWebCount: Int = 1, oppositionCount: Int = 1) {
        List(themeCount) {
            makeTheme(
                valueWebs = List(valueWebCount) {
                    makeValueWeb(
                        oppositions = List(oppositionCount) {
                            makeOppositionValue(
                                representations = listOf(SymbolicRepresentation(symbolicEntityId, originalName))
                            )
                        }
                    )
                }
            )
        }.forEach {
            themeRepository.themes[it.id] = it
        }
    }
    private fun givenThemes(themeCount: Int = 1, valueWebCount: Int = 1, oppositionCount: Int = 1)
    {
        List(themeCount) {
            makeTheme(
                valueWebs = List(valueWebCount) {
                    makeValueWeb(
                        oppositions = List(oppositionCount) {
                            makeOppositionValue()
                        }
                    )
                }
            )
        }.forEach {
            themeRepository.themes[it.id] = it
        }
    }

    private fun renameSymbolicItem()
    {
        val useCase: RenameSymbolicItem = RenameSymbolicItemUseCase(themeRepository)
        val output = object : RenameSymbolicItem.OutputPort {
            override suspend fun symbolicItemRenamed(response: List<RenamedSymbolicItem>) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(symbolicEntityId, newName, output)
        }
    }

    fun empty(actual: Any?) {
        actual as List<*>
        assertTrue(actual.isEmpty()) { "Expected to be empty" }
    }

    fun responseModel(expectedCount: Int) = fun (actual: Any?) {
        actual as List<*>
        assertTrue(actual.all { it is RenamedSymbolicItem })
        actual as List<RenamedSymbolicItem>
        assertEquals(expectedCount, actual.size)
        assertEquals(updatedThemes?.flatMap { theme ->
            theme.valueWebs.flatMap { valueWeb ->
                valueWeb.oppositions.flatMap { opposition ->
                    opposition.representations.filter { it.entityUUID == symbolicEntityId }
                        .map {
                            """
                                themeId: ${theme.id.uuid}
                                valueId: ${valueWeb.id.uuid}
                                oppositionId: ${opposition.id.uuid}
                            """.trimIndent()
                        }
                }
            }
        }?.toSet() ?: setOf<String>(), actual.map {
            """
                themeId: ${it.themeId}
                valueId: ${it.valueWebId}
                oppositionId: ${it.oppositionId}
            """.trimIndent()
        }.toSet())
    }

    fun listOfThemesOfSize(expectedSide: Int) = fun (actual: Any?) {
        actual as List<*>
        assertTrue(actual.all { it is Theme })
        actual as List<Theme>
        assertEquals(expectedSide, actual.size)
        actual.asSequence()
            .flatMap { it.valueWebs.asSequence() }
            .flatMap { it.oppositions.asSequence() }
            .flatMap { it.representations.asSequence() }
            .filter { it.entityUUID == symbolicEntityId }
            .forEach { assertEquals(newName, it.name) }
    }

}