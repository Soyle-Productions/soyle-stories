package com.soyle.stories.usecase.character.arc.section

import arrow.core.Either
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.Desire
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ArcSectionType
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterDesire
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterDesireUseCase
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.characterIsNotMajorCharacterInTheme
import com.soyle.stories.usecase.theme.characterNotInTheme
import com.soyle.stories.usecase.theme.themeDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeCharacterDesireUnitTest {

    // preconditions
    private val character = makeCharacter()
    private val theme = makeTheme()
        .withCharacterIncluded(character.id, character.name.value, character.media)
        .withCharacterPromoted(character.id)
    private val characterArc = CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name)
    private val arcSection = characterArc.arcSections.find { it.template isSameEntityAs Desire }!!

    // input
    private val themeId = theme.id.uuid
    private val characterId = character.id.uuid
    private val providedDesire = "Desire ${str()}"

    // post-conditions
    private var updatedCharacterArc: CharacterArc? = null

    // output
    private var responseModel: ChangeCharacterDesire.ResponseModel? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            changeCharacterDesire()
        } shouldBe themeDoesNotExist(themeId)
    }

    @Test
    fun `character not in theme`() {
        givenTheme()
        assertThrows<CharacterNotInTheme> {
            changeCharacterDesire()
        } shouldBe characterNotInTheme(themeId, characterId)
    }

    @Test
    fun `character is minor character`() {
        givenTheme()
        givenThemeHasCharacter()
        assertThrows<CharacterIsNotMajorCharacterInTheme> {
            changeCharacterDesire()
        } shouldBe characterIsNotMajorCharacterInTheme(themeId, characterId)
    }

    @Test
    fun `happy path`() {
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        changeCharacterDesire()
        updatedCharacterArc!!.arcSections.find { it.template isSameEntityAs Desire }!!.shouldBe {
            assertEquals(arcSection.id, it.id)
            assertEquals(providedDesire, it.value)
        }
        responseModel!!.changedCharacterDesire shouldBe ::changedCharacterDesire
    }

    @Test
    fun `linked location set`() {
        val linkedLocationId = Location.Id()
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        givenArcSectionHasLinkedLocation(linkedLocationId)
        changeCharacterDesire()
        updatedCharacterArc!!.arcSections.find { it.template isSameEntityAs Desire }!! shouldBe {
            assertEquals(arcSection.id, it.id)
            assertEquals(providedDesire, it.value)
            assertEquals(linkedLocationId, it.linkedLocation)
        }
        responseModel!!.changedCharacterDesire shouldBe ::changedCharacterDesire
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

    private fun changeCharacterDesire() {
        val useCase: ChangeCharacterDesire =
            ChangeCharacterDesireUseCase(themeRepository, characterArcRepository)
        val output = object : ChangeCharacterDesire.OutputPort {
            override suspend fun characterDesireChanged(response: ChangeCharacterDesire.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(ChangeCharacterDesire.RequestModel(themeId, characterId, providedDesire), output)
        }
    }

    private fun changedCharacterDesire(it: ChangedCharacterArcSectionValue) {
        assertEquals(themeId, it.themeId)
        assertEquals(characterId, it.characterId)
        assertEquals(arcSection.id.uuid, it.arcSectionId)
        assertEquals(ArcSectionType.Desire, it.type)
        assertEquals(providedDesire, it.newValue)
    }
}