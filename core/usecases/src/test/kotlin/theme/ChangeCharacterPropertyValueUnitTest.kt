package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.*
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.usecase.theme.changeCharacterPropertyValue.ChangeCharacterPropertyValue.Property
import com.soyle.stories.usecase.theme.changeCharacterPropertyValue.ChangeCharacterPropertyValueUseCase
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

    private var updatedTheme: Theme? = null
    private var result: Any? = null

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = ::updatedTheme::set)

    @Test
    fun `theme does not exist`() {
        givenNoThemes()
        whenUseCaseIsExecuted(Property.Archetype)
        (result as ThemeDoesNotExist).themeId.mustEqual(themeId)
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

        @Test
        fun `update ability`() {
            whenUseCaseIsExecuted(Property.Ability)
            val result = result.asValidResponseModel()
            assertEquals(Property.Ability, result.property)
        }

        @Test
        fun `persist ability`() {
            whenUseCaseIsExecuted(Property.Ability)
            val persisted = updatedTheme!!
            assertEquals(inputValue, persisted.getIncludedCharacterById(Character.Id(characterId))!!.position)
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
    }
    private fun givenThemeWithId(themeId: UUID, andCharacter: UUID? = null) {
        val theme = makeTheme(Theme.Id(themeId)).let {
            if (andCharacter == null) it
            else {
                val character = makeCharacter(Character.Id(andCharacter), Project.Id())
                it.withCharacterIncluded(character.id, character.displayName.value, character.media)
            }
        }
        themeRepository.givenTheme(theme)
    }


    private fun whenUseCaseIsExecuted(property: Property) {
        val output = object : ChangeCharacterPropertyValue.OutputPort {
            override fun receiveChangeCharacterPropertyValueFailure(failure: Exception) {
                result = failure
            }

            override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
                result = response
            }
        }
        val useCase: ChangeCharacterPropertyValue = ChangeCharacterPropertyValueUseCase(themeRepository)
        runBlocking {
            useCase.invoke(request(property), output)
        }
    }

}