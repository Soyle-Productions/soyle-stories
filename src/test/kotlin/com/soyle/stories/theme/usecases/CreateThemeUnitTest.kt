package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.themeNameCannotBeBlank
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreateThemeUseCase
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
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
    fun `name cannot be blank`() {
        whenUseCaseCalled(name = "")
        assertNoThemeAdded()
        result shouldBe ::themeNameCannotBeBlank
    }

    @Test
    fun `valid name`() {
        val name = "New Theme ${UUID.randomUUID()}"
        whenUseCaseCalled(name)
        assertThemeAddedToRepository(name)
        result shouldBe createdTheme(name)
    }

    private fun whenUseCaseCalled(name: String)
    {
        val useCase: CreateTheme = CreateThemeUseCase(ThemeRepositoryDouble(onAddTheme = { createdTheme = it }))
        val output = object : CreateTheme.OutputPort {
            override suspend fun themeCreated(response: CreatedTheme) {
                result = response
            }
        }
        runBlocking {
            try {
                useCase.invoke(projectId.uuid, name, output)
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
}