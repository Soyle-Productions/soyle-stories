package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.themeDoesNotExist
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfThemeUseCase
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExaminedCentralConflict
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ExamineCentralConflictOfThemeUnitTest {

    private val themeId = Theme.Id()

    private var examinedConflictOfTheme: ExaminedCentralConflict? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            examineCentralConflictOfTheme()
        } shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `theme exists`() {
        givenThemeExists()
        examineCentralConflictOfTheme()
        examinedConflictOfTheme!! shouldBe {
            assertEquals(themeId.uuid, it.themeId)
        }
    }

    @Test
    fun `check central conflict is output`() {
        val centralConflict = "Central Conflict ${UUID.randomUUID()}"
        givenThemeExists(centralConflict = centralConflict)
        examineCentralConflictOfTheme()
        examinedConflictOfTheme!! shouldBe {
            assertEquals(centralConflict, it.centralConflict)
        }
    }

    private val themeRepository = ThemeRepositoryDouble()

    private fun givenThemeExists(centralConflict: String = "") {
        themeRepository.themes[themeId] = makeTheme(themeId, centralConflict = centralConflict)
    }

    private val useCase: ExamineCentralConflictOfTheme = ExamineCentralConflictOfThemeUseCase(themeRepository)
    private val output = object : ExamineCentralConflictOfTheme.OutputPort {
        override suspend fun centralConflictExamined(response: ExaminedCentralConflict) {
            examinedConflictOfTheme = response
        }
    }

    fun examineCentralConflictOfTheme() = runBlocking {
        useCase.invoke(themeId.uuid, output)
    }

}