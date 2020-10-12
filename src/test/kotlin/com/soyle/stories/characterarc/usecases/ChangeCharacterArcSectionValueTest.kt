package com.soyle.stories.characterarc.usecases

import arrow.core.Either
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.*
import com.soyle.stories.common.Desire
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.theme.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeCharacterArcSectionValueTest {

    // preconditions
    private val character = makeCharacter()
    private val theme = makeTheme()
        .withCharacterIncluded(character.id, character.name, character.media)
        .withCharacterPromoted(character.id)
    private val characterArc = CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name)
    private val arcSection = characterArc.arcSections.random()

    // input
    private val themeId = theme.id.uuid
    private val characterId = character.id.uuid
    private val providedValue = "New Value ${str()}"

    // post-conditions
    private var updatedCharacterArc: CharacterArc? = null

    // output
    private var responseModel: ChangeCharacterArcSectionValue.ResponseModel? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            changeCharacterArcSectionValue()
        } shouldBe themeDoesNotExist(themeId)
    }

    @Test
    fun `character not in theme`() {
        givenTheme()
        assertThrows<CharacterNotInTheme> {
            changeCharacterArcSectionValue()
        } shouldBe characterNotInTheme(themeId, characterId)
    }

    @Test
    fun `character is minor character`() {
        givenTheme()
        givenThemeHasCharacter()
        assertThrows<CharacterIsNotMajorCharacterInTheme> {
            changeCharacterArcSectionValue()
        } shouldBe characterIsNotMajorCharacterInTheme(themeId, characterId)
    }

    @Test
    fun `happy path`() {
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        changeCharacterArcSectionValue()
        updatedCharacterArc!!.arcSections.find { it.id == arcSection.id }!!.shouldBe {
            assertEquals(providedValue, it.value)
        }
        responseModel!!.changedCharacterArcSectionValue shouldBe ::changedCharacterArcSectionValue
    }

    @Test
    fun `linked location set`() {
        val linkedLocationId = Location.Id()
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        givenArcSectionHasLinkedLocation(linkedLocationId)
        changeCharacterArcSectionValue()
        updatedCharacterArc!!.arcSections.find { it.id == arcSection.id }!! shouldBe {
            assertEquals(providedValue, it.value)
            assertEquals(linkedLocationId, it.linkedLocation)
        }
        responseModel!!.changedCharacterArcSectionValue shouldBe ::changedCharacterArcSectionValue
    }


    private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedCharacterArc::set)
    private val themeRepository = ThemeRepositoryDouble()

    private fun givenTheme() {
        themeRepository.themes[theme.id] = theme.withoutCharacter(character.id)
    }

    private fun givenThemeHasCharacter(asMajorCharacter: Boolean = false) {
        themeRepository.themes[theme.id] = if (asMajorCharacter) theme.also {
            characterArcRepository.givenCharacterArc(characterArc)
        } else {
            (theme.demoteCharacter(theme.getMajorCharacterById(character.id)!!) as Either.Right).b
        }
    }

    private fun givenArcSectionHasLinkedLocation(locationId: Location.Id) {
        runBlocking {
            characterArcRepository.givenCharacterArc(
                characterArcRepository.getCharacterArcByCharacterAndThemeId(character.id, theme.id)!!
                    .withArcSectionsMapped {
                        if (it.id == arcSection.id) it.withLinkedLocation(locationId)
                        else it
                    }
            )
        }
    }

    private fun changeCharacterArcSectionValue() {
        val useCase: ChangeCharacterArcSectionValue =
            ChangeCharacterArcSectionValueUseCase(themeRepository, characterArcRepository)
        val output = object : ChangeCharacterArcSectionValue.OutputPort {
            override suspend fun characterArcSectionValueChanged(response: ChangeCharacterArcSectionValue.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(ChangeCharacterArcSectionValue.RequestModel(themeId, characterId, arcSection.id.uuid, providedValue), output)
        }
    }

    private fun changedCharacterArcSectionValue(it: ChangedCharacterArcSectionValue) {
        assertEquals(themeId, it.themeId)
        assertEquals(characterId, it.characterId)
        assertEquals(arcSection.id.uuid, it.arcSectionId)
        assertNull(it.type)
        assertEquals(providedValue, it.newValue)
    }
}