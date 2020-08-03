package com.soyle.stories.theme.usecases

import arrow.core.Either
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.MoralWeakness
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.doubles.CharacterArcSectionRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangeCharacterDesire
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangeCharacterMoralWeakness
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangeCharacterMoralWeaknessUseCase
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import com.soyle.stories.translators.asCharacterArcSection
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeCharacterMoralWeaknessUnitTest {

    // preconditions
    private val character = makeCharacter()
    private val theme = makeTheme()
        .withCharacterIncluded(character.id, character.name, character.media)
        .withCharacterPromoted(character.id)
    private val arcSection =
        theme.getMajorCharacterById(character.id)!!.thematicSections.find { it.template.characterArcTemplateSectionId == MoralWeakness.id }!!
            .asCharacterArcSection()

    // input
    private val themeId = theme.id.uuid
    private val characterId = character.id.uuid
    private val providedWeakness = "Moral Weakness ${str()}"

    // post-conditions
    private var updatedArcSection: CharacterArcSection? = null

    // output
    private var responseModel: ChangeCharacterMoralWeakness.ResponseModel? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            changeCharacterMoralWeakness()
        } shouldBe themeDoesNotExist(themeId)
    }

    @Test
    fun `character not in theme`() {
        givenTheme()
        assertThrows<CharacterNotInTheme> {
            changeCharacterMoralWeakness()
        } shouldBe characterNotInTheme(themeId, characterId)
    }

    @Test
    fun `character is minor character`() {
        givenTheme()
        givenThemeHasCharacter()
        assertThrows<CharacterIsNotMajorCharacterInTheme> {
            changeCharacterMoralWeakness()
        } shouldBe characterIsNotMajorCharacterInTheme(themeId, characterId)
    }

    @Test
    fun `happy path`() {
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        changeCharacterMoralWeakness()
        updatedArcSection!! shouldBe {
            assertEquals(arcSection.id, it.id)
            assertEquals(providedWeakness, it.value)
        }
        responseModel!!.changedCharacterMoralWeakness shouldBe ::changedCharacterMoralWeakness
    }

    @Test
    fun `linked location set`() {
        val linkedLocationId = Location.Id()
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        givenArcSectionHasLinkedLocation(linkedLocationId)
        changeCharacterMoralWeakness()
        updatedArcSection!! shouldBe {
            assertEquals(arcSection.id, it.id)
            assertEquals(providedWeakness, it.value)
            assertEquals(linkedLocationId, it.linkedLocation)
        }
        responseModel!!.changedCharacterMoralWeakness shouldBe ::changedCharacterMoralWeakness
    }


    private val characterArcSectionRepository = CharacterArcSectionRepositoryDouble(onUpdateCharacterArcSections = {
        updatedArcSection = it
    })
    private val themeRepository = ThemeRepositoryDouble()

    private fun givenTheme() {
        themeRepository.themes[theme.id] = theme.withoutCharacter(character.id)
    }

    private fun givenThemeHasCharacter(asMajorCharacter: Boolean = false) {
        themeRepository.themes[theme.id] = if (asMajorCharacter) theme.also {
            it.getMajorCharacterById(character.id)!!.thematicSections.forEach {
                characterArcSectionRepository.characterArcSections[it.characterArcSectionId] =
                    it.asCharacterArcSection()
            }
        } else {
            (theme.demoteCharacter(theme.getMajorCharacterById(character.id)!!) as Either.Right).b
        }
    }

    private fun givenArcSectionHasLinkedLocation(locationId: Location.Id)
    {
        characterArcSectionRepository.characterArcSections[arcSection.id] = arcSection.withLinkedLocation(locationId)
    }

    private fun changeCharacterMoralWeakness() {
        val useCase: ChangeCharacterMoralWeakness =
            ChangeCharacterMoralWeaknessUseCase(themeRepository, characterArcSectionRepository)
        val output = object : ChangeCharacterMoralWeakness.OutputPort {
            override suspend fun characterMoralWeaknessChanged(response: ChangeCharacterMoralWeakness.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(ChangeCharacterMoralWeakness.RequestModel(themeId, characterId, providedWeakness), output)
        }
    }

    private fun changedCharacterMoralWeakness(it: ChangedCharacterArcSectionValue) {
        assertEquals(themeId, it.themeId)
        assertEquals(characterId, it.characterId)
        assertEquals(arcSection.id.uuid, it.arcSectionId)
        assertEquals(providedWeakness, it.newValue)
    }
}