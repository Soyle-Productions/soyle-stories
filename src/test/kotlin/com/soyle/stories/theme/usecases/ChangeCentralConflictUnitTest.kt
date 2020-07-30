package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.themeDoesNotExist
import com.soyle.stories.theme.usecases.updateThemeMetaData.ChangeCentralConflict
import com.soyle.stories.theme.usecases.updateThemeMetaData.ChangeCentralConflictUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeCentralConflictUnitTest {

    // preconditions
    private val theme = makeTheme()

    // input data
    private val themeId = theme.id.uuid
    private val providedConflict = "Central Conflict ${str()}"

    // post-conditions
    private var updatedTheme: Theme? = null

    // output data
    private var responseModel: ChangeCentralConflict.ResponseModel? = null

    @Nested
    inner class Degenerates {

        private inline fun <reified T : Throwable> degenerate(): T
        {
            val t = assertThrows<T> {
                changeCentralConflict()
            }
            assertNull(updatedTheme)
            assertNull(responseModel)
            return t
        }

        @Test
        fun `theme doesn't exist`() {
            degenerate<ThemeDoesNotExist>() shouldBe themeDoesNotExist(themeId)
        }

    }

    @Test
    fun `happy path`() {
        givenTheme()
        changeCentralConflict()
        updatedTheme!! shouldBe {
            assertEquals(theme.withCentralConflict(providedConflict), it)
        }
        responseModel!!.let {
            it.themeWithChangedCentralConflict.let {
                assertEquals(themeId, it.themeId)
                assertEquals(providedConflict, it.centralConflict)
            }
        }
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })

    private fun givenTheme()
    {
        themeRepository.themes[theme.id] = theme
    }

    private fun changeCentralConflict() {
        val useCase: ChangeCentralConflict = ChangeCentralConflictUseCase(themeRepository)
        val output = object : ChangeCentralConflict.OutputPort {
            override suspend fun centralConflictChanged(response: ChangeCentralConflict.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(themeId, providedConflict, output)
        }
    }

}