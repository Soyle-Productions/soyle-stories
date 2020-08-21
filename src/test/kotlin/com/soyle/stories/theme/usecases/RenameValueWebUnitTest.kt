package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.*
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.theme.valueWeb.ValueWebNameCannotBeBlank
import com.soyle.stories.theme.usecases.renameValueWeb.RenameValueWeb
import com.soyle.stories.theme.usecases.renameValueWeb.RenameValueWebUseCase
import com.soyle.stories.theme.usecases.renameValueWeb.RenamedValueWeb
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
    fun `invalid name`() {
        givenValueWeb()
        assertThrows<ValueWebNameCannotBeBlank> {
            renameValueWeb()
        }
        assertNull(updatedTheme)
    }

    @Test
    fun `same name`() {
        givenValueWeb()
        result = assertThrows<ValueWebAlreadyHasName> {
            renameValueWeb(originalName)
        }
        assertNull(updatedTheme)
        result shouldBe valueWebAlreadyHasName(valueWebId.uuid, originalName)
    }

    @Test
    fun `valid name`() {
        val newName = "New Value Web Name ${UUID.randomUUID().toString().takeLast(3)}"
        givenValueWeb()
        renameValueWeb(newName)
        updatedTheme shouldBe themeWithValueWebWithNewName(newName)
        result shouldBe renamedValueWeb(newName)
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenValueWeb()
    {
        themeRepository.themes[themeId] = makeTheme(themeId, valueWebs = listOf(makeValueWeb(valueWebId, themeId, originalName)))
    }

    private fun renameValueWeb(name: String = "")
    {
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
        assertEquals(name, valueWeb.name)
    }

    private fun renamedValueWeb(name: String): (Any?) -> Unit = { actual ->
        actual as RenamedValueWeb
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(valueWebId.uuid, actual.valueWebId)
        assertEquals(originalName, actual.originalName)
        assertEquals(name, actual.newName)
    }

}