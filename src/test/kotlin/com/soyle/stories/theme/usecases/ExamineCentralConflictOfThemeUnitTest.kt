package com.soyle.stories.theme.usecases

import com.soyle.stories.common.*
import com.soyle.stories.entities.*
import com.soyle.stories.theme.*
import com.soyle.stories.theme.doubles.CharacterArcSectionRepositoryDouble
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfThemeUseCase
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExaminedCentralConflict
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ExamineCentralConflictOfThemeUnitTest {

    private val themeId = Theme.Id()

    private var examinedConflictOfTheme: ExaminedCentralConflict? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            examineCentralConflictOfTheme()
        } shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `theme exists`() {
        givenThemeExists()
        examineCentralConflictOfTheme()
        examinedConflictOfTheme!! shouldBe {
            assertEquals(themeId.uuid, it.themeId)
            assertNull(it.characterChange)
        }
    }

    @Test
    fun `check central conflict is output`() {
        val centralConflict = "Central Conflict ${UUID.randomUUID()}"
        givenThemeExists(centralConflict = centralConflict)
        examineCentralConflictOfTheme()
        examinedConflictOfTheme!! shouldBe {
            assertEquals(centralConflict, it.centralConflict)
            assertNull(it.characterChange)
        }
    }

    @Nested
    inner class `Provide Character Id` {

        private val characterId = Character.Id()
        private val characterName = "Character ${UUID.randomUUID().toString().take(3)}"

        init {
            givenThemeExists()
        }

        @Test
        fun `character not in theme`() {
            assertThrows<CharacterNotInTheme> {
                examineCentralConflictOfTheme(characterId = characterId.uuid)
            } shouldBe characterNotInTheme(themeId.uuid, characterId.uuid)
        }

        @Test
        fun `character is only minor character`() {
            givenThemeHasCharacter()
            assertThrows<CharacterIsNotMajorCharacterInTheme> {
                examineCentralConflictOfTheme(characterId = characterId.uuid)
            } shouldBe characterIsNotMajorCharacterInTheme(themeId.uuid, characterId.uuid)
        }

        @Test
        fun `character is major character`() {
            givenThemeHasCharacter(isMajorCharacter = true)
            examineCentralConflictOfTheme(characterId = characterId.uuid)
            examinedConflictOfTheme!! shouldBe {
                it.characterChange!! shouldBe {
                    assertEquals(characterId.uuid, it.characterId)
                    assertEquals(characterName, it.characterName)
                    assertEquals("", it.desire)
                    assertEquals("", it.psychologicalWeakness)
                    assertEquals("", it.moralWeakness)
                    assertEquals("", it.changeAtEnd)
                }
            }
        }

        @Test
        fun `character has values for story steps`() {
            givenThemeHasCharacter(isMajorCharacter = true)
            val desire = "Desire ${str()}"
            val psychologicalWeakness = "Psychological Weakness ${str()}"
            val moralWeakness = "Moral Weakness ${str()}"
            val changeAtEnd = "Change at End ${str()}"
            givenCharacterHasValues(desire,
                psychologicalWeakness,
                moralWeakness,
                changeAtEnd
            )
            examineCentralConflictOfTheme(characterId = characterId.uuid)
            examinedConflictOfTheme!! shouldBe {
                it.characterChange!! shouldBe {
                    assertEquals(characterId.uuid, it.characterId)
                    assertEquals(characterName, it.characterName)
                    assertEquals(desire, it.desire)
                    assertEquals(psychologicalWeakness, it.psychologicalWeakness)
                    assertEquals(moralWeakness, it.moralWeakness)
                    assertEquals(changeAtEnd, it.changeAtEnd)
                }
            }
        }

        private fun givenThemeHasCharacter(isMajorCharacter: Boolean = false)
        {
            themeRepository.themes[themeId] = themeRepository.themes.getValue(themeId)
                .withCharacterIncluded(characterId, characterName, null)
                .let {
                    if (isMajorCharacter) it.withCharacterPromoted(characterId)
                    else it
                }
        }

        private fun givenCharacterHasValues(
            desire: String,
            psychologicalWeakness: String,
            moralWeakness: String,
            changeAtEnd: String
        ) {
            themeRepository.themes[themeId] = themeRepository.themes.getValue(themeId).let { theme ->
                runBlocking {
                    characterArcSectionRepository.addNewCharacterArcSections(listOf(
                        CharacterArcSection(CharacterArcSection.Id(UUID.randomUUID()), characterId, themeId, Desire, null, desire),
                        CharacterArcSection(CharacterArcSection.Id(UUID.randomUUID()), characterId, themeId, PsychologicalWeakness, null, psychologicalWeakness),
                        CharacterArcSection(CharacterArcSection.Id(UUID.randomUUID()), characterId, themeId, MoralWeakness, null, moralWeakness)
                    ))
                }
                theme.withCharacterChangeAs(characterId, changeAtEnd)
            }
        }

    }

    private val themeRepository = ThemeRepositoryDouble()
    private val characterArcSectionRepository = CharacterArcSectionRepositoryDouble()

    private fun givenThemeExists(centralConflict: String = "") {
        themeRepository.themes[themeId] = makeTheme(themeId, centralConflict = centralConflict)
    }

    private val useCase: ExamineCentralConflictOfTheme = ExamineCentralConflictOfThemeUseCase(themeRepository, characterArcSectionRepository)
    private val output = object : ExamineCentralConflictOfTheme.OutputPort {
        override suspend fun centralConflictExamined(response: ExaminedCentralConflict) {
            examinedConflictOfTheme = response
        }
    }

    fun examineCentralConflictOfTheme(characterId: UUID? = null) = runBlocking {
        useCase.invoke(themeId.uuid, characterId, output)
    }

}