package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.theme.makeValueWeb
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.renameValueWeb.RenameValueWeb
import com.soyle.stories.usecase.theme.renameValueWeb.RenameValueWebUseCase
import com.soyle.stories.usecase.theme.renameValueWeb.RenamedValueWeb
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RenameValueWebUnitTest {

    private val themeId = Theme.Id()
    private val valueWebId = ValueWeb.Id()
    private val originalName = "Original Value Web Name ${UUID.randomUUID().toString().takeLast(3)}"

    private var updatedTheme: Theme? = null
    private var result: Any? = null

    @Test
    fun `value web does not exist`() {
        result = assertThrows<ValueWebDoesNotExist> {
            renameValueWeb()
        }
        assertNull(updatedTheme)
        result shouldBe valueWebDoesNotExist(valueWebId.uuid)
    }

    @Test
    fun `same name`() {
        givenValueWeb()
        result = assertThrows<ValueWebAlreadyHasName> {
            renameValueWeb(nonBlankStr(originalName))
        }
        assertNull(updatedTheme)
        result shouldBe valueWebAlreadyHasName(valueWebId.uuid, originalName)
    }

    @Test
    fun `valid name`() {
        val newName = nonBlankStr("New Value Web Name ${UUID.randomUUID().toString().takeLast(3)}")
        givenValueWeb()
        renameValueWeb(newName)
        updatedTheme shouldBe themeWithValueWebWithNewName(newName.value)
        result shouldBe renamedValueWeb(newName.value)
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenValueWeb() {
        themeRepository.themes[themeId] =
            makeTheme(themeId, valueWebs = listOf(makeValueWeb(valueWebId, themeId, nonBlankStr(originalName))))
    }

    private fun renameValueWeb(name: NonBlankString = nonBlankStr()) {
        val useCase: RenameValueWeb = RenameValueWebUseCase(themeRepository)
        val output = object : RenameValueWeb.OutputPort {
            override suspend fun valueWebRenamed(response: RenamedValueWeb) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(valueWebId.uuid, name, output)
        }
    }

    private fun themeWithValueWebWithNewName(name: String): (Any?) -> Unit = { actual ->
        actual as Theme
        assertEquals(themeId, actual.id)
        val valueWeb = actual.valueWebs.find { it.id == valueWebId }!!
        assertEquals(name, valueWeb.name.value)
    }

    private fun renamedValueWeb(name: String): (Any?) -> Unit = { actual ->
        actual as RenamedValueWeb
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(valueWebId.uuid, actual.valueWebId)
        assertEquals(originalName, actual.originalName)
        assertEquals(name, actual.newName)
    }

}