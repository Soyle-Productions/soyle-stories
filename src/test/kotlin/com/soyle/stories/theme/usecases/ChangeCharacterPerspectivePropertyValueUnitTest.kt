package com.soyle.stories.theme.usecases

import arrow.core.Either
import arrow.core.identity
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue.Property
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValueUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ChangeCharacterPerspectivePropertyValueUnitTest {

    val themeId: UUID = UUID.randomUUID()
    val focalCharacterId: UUID = UUID.randomUUID()
    val targetCharacterId: UUID = UUID.randomUUID()
    private val inputValue = "I should be the new value!"
    private fun request(property: Property) =
        ChangeCharacterPerspectivePropertyValue.RequestModel(
            themeId,
            focalCharacterId,
            targetCharacterId,
            property,
            inputValue
        )

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
        whenUseCaseIsExecuted(Property.Attack)
        (result as ThemeDoesNotExist).themeIdMustEqual(themeId)
    }

    @Test
    fun `focal character not in theme`() {
        givenThemeWith(themeId = themeId)
        whenUseCaseIsExecuted(Property.Attack)
        val result = result as CharacterNotInTheme
        assertEquals(themeId, result.themeId)
        assertEquals(focalCharacterId, result.characterId)
    }

    @Test
    fun `focal character in minor character`() {
        givenThemeWith(themeId = themeId, andCharacters = *arrayOf(focalCharacterId))
        whenUseCaseIsExecuted(Property.Attack)
        val result = result as CharacterIsNotMajorCharacterInTheme
        assertEquals(themeId, result.themeId)
        assertEquals(focalCharacterId, result.characterId)
    }

    @Test
    fun `target character not in theme`() {
        givenThemeWith(
            themeId = themeId,
            andCharacters = *arrayOf(focalCharacterId),
            andMajorCharacter = focalCharacterId
        )
        whenUseCaseIsExecuted(Property.Attack)
        val result = result as CharacterNotInTheme
        assertEquals(themeId, result.themeId)
        assertEquals(targetCharacterId, result.characterId)
    }

    @Nested
    inner class SuccessfulUpdates {

        @BeforeEach
        fun givenThemeAndCharacters() {
            givenThemeWith(
                themeId = themeId,
                andCharacters = *arrayOf(focalCharacterId, targetCharacterId),
                andMajorCharacter = focalCharacterId
            )
        }

        @Test
        fun `update attack`() {
            whenUseCaseIsExecuted(Property.Attack)
            val result = result.asValidResponseModel()
            assertEquals(Property.Attack, result.property)
        }

        @Test
        fun `persist attack`() {
            whenUseCaseIsExecuted(Property.Attack)
            val persisted = updatedTheme!!
            assertEquals(
                inputValue,
                persisted.getMajorCharacterById(Character.Id(focalCharacterId))!!
                    .getAttacksByCharacter(Character.Id(targetCharacterId))
            )
        }

        @Test
        fun `update similarities`() {
            whenUseCaseIsExecuted(Property.Similarities)
            val result = result.asValidResponseModel()
            assertEquals(Property.Similarities, result.property)
        }

        @Test
        fun `persist similarities`() {
            whenUseCaseIsExecuted(Property.Similarities)
            val persisted = updatedTheme!!
            val (similarities) = persisted.getSimilarities(
                Character.Id(focalCharacterId),
                Character.Id(targetCharacterId)
            ) as Either.Right
            assertEquals(inputValue, similarities)
        }


        fun Any?.asValidResponseModel(): ChangeCharacterPerspectivePropertyValue.ResponseModel {
            this as ChangeCharacterPerspectivePropertyValue.ResponseModel
            assertEquals(this@ChangeCharacterPerspectivePropertyValueUnitTest.themeId, this.themeId)
            assertEquals(
                this@ChangeCharacterPerspectivePropertyValueUnitTest.focalCharacterId,
                this.perspectiveCharacterId
            )
            assertEquals(this@ChangeCharacterPerspectivePropertyValueUnitTest.targetCharacterId, this.targetCharacterId)
            assertEquals(this@ChangeCharacterPerspectivePropertyValueUnitTest.inputValue, this.newValue)
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

    private fun givenThemeWith(themeId: UUID, andMajorCharacter: UUID? = null, vararg andCharacters: UUID) {
        val theme = Theme(Theme.Id(themeId), "", emptyMap(), emptyMap()).let {
            andCharacters.fold(it) { nextTheme, characterId ->
                nextTheme.includeCharacter(Character(Character.Id(characterId), UUID.randomUUID(), "Bob"))
                    .fold({ throw it }, ::identity)
            }
        }.let { theme ->
            if (andMajorCharacter != null) theme.getMinorCharacterById(Character.Id(andMajorCharacter))?.let {
                theme.promoteCharacter(it)
                    .fold({ throw it }, ::identity)
            } ?: theme else theme
        }
        context = setupContext(
            initialThemes = listOf(theme),
            updateTheme = {
                updatedTheme = it
            }
        )
    }


    private fun whenUseCaseIsExecuted(property: Property) {
        val output = object : ChangeCharacterPerspectivePropertyValue.OutputPort {
            override fun receiveChangeCharacterPerspectivePropertyValueFailure(failure: ThemeException) {
                result = failure
            }

            override fun receiveChangeCharacterPerspectivePropertyValueResponse(response: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
                result = response
            }
        }
        val useCase: ChangeCharacterPerspectivePropertyValue = ChangeCharacterPerspectivePropertyValueUseCase(context)
        runBlocking {
            useCase.invoke(request(property), output)
        }
    }

}