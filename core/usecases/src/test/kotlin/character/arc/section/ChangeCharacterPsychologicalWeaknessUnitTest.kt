package com.soyle.stories.usecase.character.arc.section

import arrow.core.Either
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.PsychologicalWeakness
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.character.makeCharacterArcSection
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ArcSectionType
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeakness
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeaknessUseCase
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

class ChangeCharacterPsychologicalWeaknessUnitTest {

    // preconditions
    private val character = makeCharacter()
    private val theme = makeTheme()
        .withCharacterIncluded(character.id, character.name.value, character.media)
        .withCharacterPromoted(character.id)
    private val arcSection = makeCharacterArcSection(characterId = character.id, themeId = theme.id, template = PsychologicalWeakness)
    private val characterArc = CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name)
        .withArcSection(arcSection)

    // input
    private val themeId = theme.id.uuid
    private val characterId = character.id.uuid
    private val providedWeakness = "Psychological Weakness ${str()}"

    // post-conditions
    private var updatedCharacterArc: CharacterArc? = null

    // output
    private var responseModel: ChangeCharacterPsychologicalWeakness.ResponseModel? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            changeCharacterPsychologicalWeakness()
        } shouldBe themeDoesNotExist(themeId)
    }

    @Test
    fun `character not in theme`() {
        givenTheme()
        assertThrows<CharacterNotInTheme> {
            changeCharacterPsychologicalWeakness()
        } shouldBe characterNotInTheme(themeId, characterId)
    }

    @Test
    fun `character is minor character`() {
        givenTheme()
        givenThemeHasCharacter()
        assertThrows<CharacterIsNotMajorCharacterInTheme> {
            changeCharacterPsychologicalWeakness()
        } shouldBe characterIsNotMajorCharacterInTheme(themeId, characterId)
    }

    @Test
    fun `happy path`() {
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        changeCharacterPsychologicalWeakness()
        updatedCharacterArc!!.arcSections.find { it.id == arcSection.id }!! shouldBe {
            assertEquals(arcSection.id, it.id)
            assertEquals(providedWeakness, it.value)
        }
        responseModel!!.changedCharacterPsychologicalWeakness shouldBe ::changedCharacterPsychologicalWeakness
    }

    @Test
    fun `linked location set`() {
        val linkedLocationId = Location.Id()
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        givenArcSectionHasLinkedLocation(linkedLocationId)
        changeCharacterPsychologicalWeakness()
        updatedCharacterArc!!.arcSections.find { it.id == arcSection.id }!! shouldBe {
            assertEquals(arcSection.id, it.id)
            assertEquals(providedWeakness, it.value)
            assertEquals(linkedLocationId, it.linkedLocation)
        }
        responseModel!!.changedCharacterPsychologicalWeakness shouldBe ::changedCharacterPsychologicalWeakness
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

    private fun givenArcSectionHasLinkedLocation(locationId: Location.Id)
    {
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

    private fun changeCharacterPsychologicalWeakness() {
        val useCase: ChangeCharacterPsychologicalWeakness =
            ChangeCharacterPsychologicalWeaknessUseCase(themeRepository, characterArcRepository)
        val output = object : ChangeCharacterPsychologicalWeakness.OutputPort {
            override suspend fun characterPsychologicalWeaknessChanged(response: ChangeCharacterPsychologicalWeakness.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(ChangeCharacterPsychologicalWeakness.RequestModel(themeId, characterId, providedWeakness), output)
        }
    }

    private fun changedCharacterPsychologicalWeakness(it: ChangedCharacterArcSectionValue) {
        assertEquals(themeId, it.themeId)
        assertEquals(characterId, it.characterId)
        assertEquals(arcSection.id.uuid, it.arcSectionId)
        assertEquals(ArcSectionType.PsychologicalWeakness, it.type)
        assertEquals(providedWeakness, it.newValue)
    }
}