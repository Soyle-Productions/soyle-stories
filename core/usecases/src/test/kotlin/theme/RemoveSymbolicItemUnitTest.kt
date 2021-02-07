package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.*
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemoveSymbolicItem
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemoveSymbolicItemUseCase
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemovedSymbolicItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RemoveSymbolicItemUnitTest {

    private val symbolicItemId = UUID.randomUUID()

    private var updatedThemes: List<Theme>? = null
    private var result: Any? = null

    private var themesWithEntity: List<Theme> = listOf()

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedThemes = (updatedThemes ?: listOf()) + it
    })

    @Nested
    inner class `Remove from Single Opposition`
    {
        private val themeId = Theme.Id()
        private val valueWebId = ValueWeb.Id()
        private val oppositionId = OppositionValue.Id()

        @Test
        fun `opposition doesn't exist`() {
            result = assertThrows<OppositionValueDoesNotExist> {
                removeSymbolicItemFromOpposition()
            }
            result shouldBe oppositionValueDoesNotExist(oppositionId.uuid)
            assertNull(updatedThemes)
        }

        @Test
        fun `opposition does not have item`() {
            givenOpposition()
            result = assertThrows<SymbolicRepresentationNotInOppositionValue> {
                removeSymbolicItemFromOpposition()
            }
            result shouldBe symbolicRepresentationNotInOppositionValue(oppositionId.uuid, symbolicItemId)
            assertNull(updatedThemes)
        }

        @Test
        fun `opposition has item`() {
            givenOpposition(hasItem = true)
            removeSymbolicItemFromOpposition()
            updatedThemes shouldBe {
                it as List<Theme>
                val updatedTheme = it.single()
                assertEquals(updatedTheme.id, themeId)
                val updatedOpposition = updatedTheme.valueWebs.asSequence().flatMap { it.oppositions.asSequence() }.find { it.id == oppositionId }!!
                assertNull(updatedOpposition.representations.find { it.entityUUID == symbolicItemId })
            }
            result shouldBe {
                it as List<*>
                it.single() shouldBe removedSymbolicItem(themeId.uuid, valueWebId.uuid, oppositionId.uuid, symbolicItemId)
            }
        }

        private fun removeSymbolicItemFromOpposition()
        {
            val useCase: RemoveSymbolicItem = RemoveSymbolicItemUseCase(themeRepository)
            val output = object : RemoveSymbolicItem.OutputPort {
                override suspend fun symbolicItemsRemoved(response: List<RemovedSymbolicItem>) {
                    result = response
                }
            }
            runBlocking {
                useCase.removeSymbolicItemFromOpposition(oppositionId.uuid, symbolicItemId, output)
            }
        }

        private fun givenOpposition(hasItem: Boolean = false)
        {
            makeTheme(themeId, valueWebs = listOf(
                makeValueWeb(valueWebId, oppositions = listOf(
                    makeOppositionValue(oppositionId, representations = listOfNotNull(
                        if (hasItem) SymbolicRepresentation(symbolicItemId, "") else null
                    ))
                ))
            )).let {
                themeRepository.themes[it.id] = it
            }
        }
    }

    @Nested
    inner class `Remove from All Oppositions`
    {

        @Nested
        inner class `No themes have the entity as a symbolic item` {

            @AfterEach
            fun `assert empty`() {
                removeSymbolicItemFromAllThemes()
                assertNull(updatedThemes)
                result shouldBe ::emptyResponse
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
            givenThemes(withEntity = true)
            removeSymbolicItemFromAllThemes()
            updatedThemes shouldBe listOfThemesOfSize(1)
            result shouldBe responseModel(1)
        }

        @Test
        fun `all oppositions per value web per theme should be output`() {
            givenThemes(themeCount = 3, valueWebCount = 4, oppositionCount = 5, withEntity = true)
            removeSymbolicItemFromAllThemes()
            updatedThemes shouldBe listOfThemesOfSize(3)
            result shouldBe responseModel(12)
        }

        @Test
        fun `only oppositions with entity output`() {
            givenThemes(themeCount = 2, valueWebCount = 3, oppositionCount = 4, withEntity = true)
            givenThemes(themeCount = 3, valueWebCount = 4, oppositionCount = 5, withEntity = false)
            removeSymbolicItemFromAllThemes()
            updatedThemes shouldBe listOfThemesOfSize(2)
            result shouldBe responseModel(6)
        }

        private fun removeSymbolicItemFromAllThemes()
        {
            val useCase: RemoveSymbolicItem = RemoveSymbolicItemUseCase(themeRepository)
            val output = object : RemoveSymbolicItem.OutputPort {
                override suspend fun symbolicItemsRemoved(response: List<RemovedSymbolicItem>) {
                    result = response
                }
            }
            runBlocking {
                useCase.removeSymbolicItemFromAllThemes(symbolicItemId, output)
            }
        }

        private fun givenThemes(themeCount: Int = 1, valueWebCount: Int = 1, oppositionCount: Int = 1, withEntity: Boolean = false) {
            val themeList = List(themeCount) {
                makeTheme(
                    valueWebs = List(valueWebCount) {
                        makeValueWeb(
                            oppositions = List(oppositionCount) {
                                makeOppositionValue(representations = listOfNotNull(
                                    if (withEntity && it == 0) SymbolicRepresentation(symbolicItemId, "") else null
                                ))
                            }
                        )
                    }
                )
            }
            if (withEntity) themesWithEntity = themeList
            themeList.forEach {
                themeRepository.themes[it.id] = it
            }
        }
    }

    private fun emptyResponse(actual: Any?) {
        actual as List<*>
        assertTrue(actual.isEmpty())
    }

    fun responseModel(expectedCount: Int) = fun (actual: Any?) {
        actual as List<*>
        assertTrue(actual.all { it is RemovedSymbolicItem })
        actual as List<RemovedSymbolicItem>
        assertEquals(expectedCount, actual.size)
        assertEquals(themesWithEntity.flatMap { theme ->
            theme.valueWebs.flatMap { valueWeb ->
                valueWeb.oppositions.flatMap { opposition ->
                    opposition.representations.filter { it.entityUUID == symbolicItemId }
                        .map {
                            """
                                themeId: ${theme.id.uuid}
                                valueId: ${valueWeb.id.uuid}
                                oppositionId: ${opposition.id.uuid}
                            """.trimIndent()
                        }
                }
            }
        }.toSet(), actual.map {
            """
                themeId: ${it.themeId}
                valueId: ${it.valueWebId}
                oppositionId: ${it.oppositionValueId}
            """.trimIndent()
        }.toSet())
    }

    fun listOfThemesOfSize(expectedSide: Int) = fun (actual: Any?) {
        actual as List<*>
        assertTrue(actual.all { it is Theme })
        actual as List<Theme>
        assertEquals(expectedSide, actual.size)
        assertTrue(actual.asSequence()
            .flatMap { it.valueWebs.asSequence() }
            .flatMap { it.oppositions.asSequence() }
            .flatMap { it.representations.asSequence() }
            .filter { it.entityUUID == symbolicItemId }
            .toList().isEmpty())
    }
}

fun removedSymbolicItem(themeId: UUID, valueWebId: UUID, oppositionId: UUID, symbolicItemId: UUID) = fun (actual: Any?) {
    actual as RemovedSymbolicItem
    assertEquals(themeId, actual.themeId)
    assertEquals(valueWebId, actual.valueWebId)
    assertEquals(oppositionId, actual.oppositionValueId)
    assertEquals(symbolicItemId, actual.symbolicItemId)
}