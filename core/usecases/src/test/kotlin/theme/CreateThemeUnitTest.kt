package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.usecase.theme.createTheme.CreateTheme
import com.soyle.stories.usecase.theme.createTheme.CreateThemeUseCase
import com.soyle.stories.usecase.theme.createTheme.CreatedTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class CreateThemeUnitTest {

    /*

    This use case allows the client to specify a name
    validates the name
    adds a new theme to the theme repository with the specified name
    outputs the new theme id and name

     */

    private val projectId = Project.Id()

    private var createdTheme: Theme? = null
    private var result: Any? = null

    @Test
    fun `create with valid first symbol name`() {
        val name = nonBlankStr("New Theme ${UUID.randomUUID()}")
        val symbolName = nonBlankStr("New Symbol ${UUID.randomUUID()}")
        whenUseCaseCalled(name, symbolName)
        assertThemeAddedToRepository(name.value)
        result shouldBe {
            it as List<*>
            it.first() shouldBe createdTheme(name.value)
            it.component2() shouldBe symbolAddedToTheme(symbolName.value)
        }
    }

    private fun whenUseCaseCalled(name: NonBlankString, firstSymbolName: NonBlankString? = null)
    {
        val useCase: CreateTheme = CreateThemeUseCase(ThemeRepositoryDouble(onAddTheme = { createdTheme = it }))
        val output = object : CreateTheme.OutputPort {
            override suspend fun themeCreated(response: CreatedTheme) {
                result = response
            }

            override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
                if (result != null) result = listOf(result, response)
                else result = response
            }
        }
        val request = CreateTheme.RequestModel(projectId.uuid, name, firstSymbolName)
        runBlocking {
            try {
                useCase.invoke(request, output)
            } catch (t: Throwable) {
                result = t
            }
        }
    }

    private fun assertNoThemeAdded() {
        assertNull(createdTheme)
    }

    private fun assertThemeAddedToRepository(expectedName: String)
    {
        val actual = createdTheme!!
        assertEquals(projectId, actual.projectId)
        assertEquals(expectedName, actual.name)
    }

    private fun createdTheme(expectedName: String): (Any?) -> Unit = { actual: Any?  ->
        actual as CreatedTheme
        assertEquals(projectId.uuid, actual.projectId)
        assertEquals(createdTheme!!.id.uuid, actual.themeId)
        assertEquals(expectedName, actual.themeName)
    }

    private fun symbolAddedToTheme(expectedName: String): (Any?) -> Unit = { actual: Any?  ->
        actual as SymbolAddedToTheme
        assertEquals(createdTheme!!.id.uuid, actual.themeId)
        assertEquals(createdTheme!!.symbols.single().id.uuid, actual.symbolId)
        assertEquals(expectedName, actual.symbolName)
    }
}