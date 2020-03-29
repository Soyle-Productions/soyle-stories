package com.soyle.stories.theme.usecases

import arrow.core.identity
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue.Property
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValueUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ChangeCharacterPropertyValueUnitTest {

    val themeId: UUID = UUID.randomUUID()
    val characterId: UUID = UUID.randomUUID()
    private val inputValue = "I should be the new value!"
    private fun request(property: Property) =
        ChangeCharacterPropertyValue.RequestModel(themeId, characterId, property, inputValue)

    private lateinit var context: Context
    private var updatedTheme: Theme? = null
    private var result: Any? = null

    @BeforeEach
    fun clear() {
        result = null
        context = setupContext()
        updatedTheme = null
    }

    @Test
    fun `theme does not exist`() {
        givenNoThemes()
        whenUseCaseIsExecuted(Property.Archetype)
        (result as ThemeDoesNotExist).themeIdMustEqual(themeId)
    }

    @Test
    fun `character not in theme`() {
        givenThemeWithId(themeId)
        whenUseCaseIsExecuted(Property.Archetype)
        val result = result as CharacterNotInTheme
        assertEquals(themeId, result.themeId)
        assertEquals(characterId, result.characterId)
    }

    @Nested
    inner class SuccessfulUpdates {

        @BeforeEach
        fun givenThemeAndCharacter() {
            givenThemeWithId(themeId, andCharacter = characterId)
        }

        @Test
        fun `update archetype`() {
            whenUseCaseIsExecuted(Property.Archetype)
            val result = result.asValidResponseModel()
            assertEquals(Property.Archetype, result.property)
        }

        @Test
        fun `persist archetype`() {
            whenUseCaseIsExecuted(Property.Archetype)
            val persisted = updatedTheme!!
            assertEquals(inputValue, persisted.getIncludedCharacterById(Character.Id(characterId))!!.archetype)
        }

        @Test
        fun `update moral variation`() {
            whenUseCaseIsExecuted(Property.VariationOnMoral)
            val result = result.asValidResponseModel()
            assertEquals(Property.VariationOnMoral, result.property)
        }

        @Test
        fun `persist moral variation`() {
            whenUseCaseIsExecuted(Property.VariationOnMoral)
            val persisted = updatedTheme!!
            assertEquals(inputValue, persisted.getIncludedCharacterById(Character.Id(characterId))!!.variationOnMoral)
        }

        fun Any?.asValidResponseModel(): ChangeCharacterPropertyValue.ResponseModel {
            this as ChangeCharacterPropertyValue.ResponseModel
            assertEquals(this@ChangeCharacterPropertyValueUnitTest.themeId, this.themeId)
            assertEquals(this@ChangeCharacterPropertyValueUnitTest.characterId, this.characterId)
            assertEquals(this@ChangeCharacterPropertyValueUnitTest.inputValue, this.newValue)
            return this
        }
    }

    private fun givenNoThemes() {
        context = setupContext(
            initialThemes = emptyList(),
            updateTheme = {
                updatedTheme = it
            }
        )
    }
    private fun givenThemeWithId(themeId: UUID, andCharacter: UUID? = null) {
        val theme = Theme(Theme.Id(themeId), "", emptyMap(), emptyMap()).let {
            if (andCharacter == null) it
            else it.includeCharacter(Character(Character.Id(andCharacter), UUID.randomUUID(), "Bob")).fold({ throw it }, ::identity)
        }
        context = setupContext(
            initialThemes = listOf(theme),
            updateTheme = {
                updatedTheme = it
            }
        )
    }


    private fun whenUseCaseIsExecuted(property: Property) {
        val output = object : ChangeCharacterPropertyValue.OutputPort {
            override fun receiveChangeCharacterPropertyValueFailure(failure: ThemeException) {
                result = failure
            }

            override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
                result = response
            }
        }
        val useCase: ChangeCharacterPropertyValue = ChangeCharacterPropertyValueUseCase(context)
        runBlocking {
            useCase.invoke(request(property), output)
        }
    }

}