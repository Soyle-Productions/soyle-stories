package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.themeDoesNotExist
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToThemeUseCase
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToThemeUseCase
import com.soyle.stories.theme.usecases.addValueWebToTheme.ValueWebAddedToTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class AddValueWebToThemeUnitTest {

    private val themeId = Theme.Id()

    private var updatedTheme: Theme? = null
    private var result: Any? = null

    @Test
    fun `theme does not exist`() {
        whenValueWebIsAddedToTheme()
        result shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `blank value web name`() {
        givenThemeExists()
        whenValueWebIsAddedToTheme()
        result shouldBe ::valueWebNameCannotBeBlank
    }

    @Nested
    inner class `Valid Value Web Name` {

        val name = "Valid Value Web Name ${UUID.randomUUID()}"

        init {
            givenThemeExists()
            whenValueWebIsAddedToTheme(name)
        }

        @Test
        fun `check value web created correctly`() {
            val updatedTheme = updatedTheme!!
            assertEquals(themeId, updatedTheme.id)
            val createdValueWeb = updatedTheme.valueWebs.single()
            assertEquals(name, createdValueWeb.name)
            val firstOpposition = createdValueWeb.oppositions.single()
            assertEquals(name, firstOpposition.name)
        }

        @Test
        fun `check output`() {
            val actual = result as ValueWebAddedToTheme
            assertEquals(themeId.uuid, actual.themeId)
            val createdValueWeb = updatedTheme!!.valueWebs.single()
            assertEquals(createdValueWeb.id.uuid, actual.valueWebId)
            assertEquals(name, actual.valueWebName)
            assertEquals(themeId.uuid, actual.oppositionAddedToValueWeb.themeId)
            assertEquals(createdValueWeb.id.uuid, actual.oppositionAddedToValueWeb.valueWebId)
            assertEquals(createdValueWeb.oppositions.single().id.uuid, actual.oppositionAddedToValueWeb.oppositionValueId)
            assertEquals(name, actual.oppositionAddedToValueWeb.oppositionValueName)
        }
    }

    @Test
    fun `add another value web`() {
        val existingValueWebCount = 3
        givenThemeExists(existingValueWebCount)
        whenValueWebIsAddedToTheme("Valid name")
        assertEquals(existingValueWebCount + 1, updatedTheme!!.valueWebs.size)
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenThemeExists(valueWebCount: Int = 0)
    {
        themeRepository.themes[themeId] = makeTheme(themeId, valueWebs = List(valueWebCount) {
            ValueWeb(themeId, "Value Web $it")
        })
    }

    private fun whenValueWebIsAddedToTheme(name: String = "")
    {
        val useCase: AddValueWebToTheme = AddValueWebToThemeUseCase(themeRepository)
        val output = object : AddValueWebToTheme.OutputPort {
            override suspend fun addedValueWebToTheme(response: ValueWebAddedToTheme) {
                result = response
            }
        }
        runBlocking {
            try { useCase.invoke(themeId.uuid, name, output) }
            catch (t: Throwable) { result = t }
        }
    }

    private fun valueWebNameCannotBeBlank(actual: Any?)
    {
        actual as ValueWebNameCannotBeBlank
    }

}