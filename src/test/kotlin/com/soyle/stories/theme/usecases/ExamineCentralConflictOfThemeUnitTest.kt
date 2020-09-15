package com.soyle.stories.theme.usecases

import arrow.core.Either
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.character.makeCharacterArcSection
import com.soyle.stories.common.*
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.characterInTheme.StoryFunction
import com.soyle.stories.theme.*
import com.soyle.stories.doubles.CharacterArcSectionRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.theme.characterInTheme.CharacterInTheme
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
                    assertTrue(it.opponents.isEmpty())
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
            givenCharacterHasValues(
                desire,
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
                    assertTrue(it.opponents.isEmpty())
                }
            }
        }

        @Test
        fun `character has opponents`() {
            givenThemeHasCharacter(isMajorCharacter = true)
            givenCharacterHasOpponents(count = 4)
            examineCentralConflictOfTheme(characterId = characterId.uuid)
            examinedConflictOfTheme!! shouldBe {
                it.characterChange!! shouldBe {
                    assertFalse(it.opponents.isEmpty())
                    assertEquals(4, it.opponents.size)
                    val theme = themeRepository.themes[themeId]!!
                    val opponents = theme.getMajorCharacterById(characterId)!!.getOpponents().map {
                        theme.getIncludedCharacterById(it.key)!!
                    }.associateBy { it.id.uuid }
                    it.opponents.forEach {
                        val character = opponents.getValue(it.characterId)
                        assertEquals(character.name, it.characterName)
                        assertEquals("", it.attack)
                        assertEquals("", it.similarities)
                        assertEquals("", it.powerStatusOrAbility)
                        assertEquals(false, it.isMainOpponent)
                    }
                }
            }
        }

        @Test
        fun `opponents have values for conflict fields`() {
            givenThemeHasCharacter(isMajorCharacter = true)
            givenCharacterHasOpponents(
                count = 4,
                generateAttack = { "Attack $it" },
                generateSimilarities = { "We're Similar $it" },
                generatePosition = { "Position $it" }
            )
            val mainOpponent = givenCharacterHasMainOpponent()
            examineCentralConflictOfTheme(characterId = characterId.uuid)
            examinedConflictOfTheme!! shouldBe {
                it.characterChange!! shouldBe {
                    assertFalse(it.opponents.isEmpty())
                    assertEquals(4, it.opponents.size)
                    val theme = themeRepository.themes[themeId]!!
                    val majorCharacter = theme.getMajorCharacterById(characterId)!!
                    val opponents = majorCharacter.getOpponents().map {
                        theme.getIncludedCharacterById(it.key)!!
                    }.associateBy { it.id.uuid }
                    it.opponents.forEach {
                        val character = opponents.getValue(it.characterId)
                        assertEquals(character.name, it.characterName)
                        assertEquals(majorCharacter.getAttacksByCharacter(character.id)!!, it.attack)
                        assertEquals(
                            (theme.getSimilarities(characterId, character.id) as Either.Right).b,
                            it.similarities
                        )
                        assertEquals(character.position, it.powerStatusOrAbility)
                        assertEquals(character.id == mainOpponent.id, it.isMainOpponent)
                    }
                }
            }
        }

        private fun givenThemeHasCharacter(isMajorCharacter: Boolean = false) {
            themeRepository.themes[themeId] = themeRepository.themes.getValue(themeId)
                .withCharacterIncluded(characterId, characterName, null)
                .let {
                    if (isMajorCharacter) {
                        characterArcRepository.givenCharacterArc(CharacterArc.planNewCharacterArc(characterId, themeId, it.name))
                        it.withCharacterPromoted(characterId)
                    }
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
                val arc = CharacterArc.planNewCharacterArc(
                    characterId,
                    themeId,
                    theme.name,
                    template = CharacterArcTemplate(listOf(Desire, PsychologicalWeakness, MoralWeakness))
                )
                    .withArcSection(makeCharacterArcSection(characterId = characterId, template = PsychologicalWeakness))
                    .withArcSection(makeCharacterArcSection(characterId = characterId, template = MoralWeakness))
                    .withArcSectionsMapped {
                    when {
                        it.template isSameEntityAs Desire -> it.withValue(desire)
                        it.template isSameEntityAs PsychologicalWeakness -> it.withValue(psychologicalWeakness)
                        it.template isSameEntityAs MoralWeakness -> it.withValue(moralWeakness)
                        else -> it
                    }
                }
                characterArcRepository.givenCharacterArc(arc)
                theme.withCharacterChangeAs(characterId, changeAtEnd)
            }
        }

        private fun givenCharacterHasOpponents(
            count: Int,
            generateAttack: (Int) -> String = { "" },
            generateSimilarities: (Int) -> String = { "" },
            generatePosition: (Int) -> String = { "" }
        ) {
            themeRepository.themes[themeId] = themeRepository.themes.getValue(themeId).let {
                (1..count).fold(it) { theme, i ->
                    val opponent = makeCharacter()
                    theme.withCharacterIncluded(opponent.id, opponent.name, opponent.media)
                        .withCharacterAsStoryFunctionForMajorCharacter(
                            opponent.id,
                            StoryFunction.Antagonist,
                            characterId
                        )
                        .withCharacterAttackingMajorCharacter(opponent.id, generateAttack(i), characterId)
                        .withCharactersSimilarToEachOther(coupleOf(opponent.id, characterId), generateSimilarities(i))
                        .withCharacterHoldingPosition(opponent.id, generatePosition(i))
                }
            }
        }

        private fun givenCharacterHasMainOpponent(): CharacterInTheme {
            val theme = themeRepository.themes.getValue(themeId)
            val opponentCharacter = theme.characters.find { it.id != characterId }!!
            themeRepository.themes[themeId] = theme.withCharacterAsStoryFunctionForMajorCharacter(
                opponentCharacter.id,
                StoryFunction.MainAntagonist,
                characterId
            )
            return opponentCharacter
        }

    }

    private val themeRepository = ThemeRepositoryDouble()
    private val characterArcRepository = CharacterArcRepositoryDouble()

    private fun givenThemeExists(centralConflict: String = "") {
        themeRepository.themes[themeId] = makeTheme(themeId, centralConflict = centralConflict)
    }

    private val useCase: ExamineCentralConflictOfTheme =
        ExamineCentralConflictOfThemeUseCase(themeRepository, characterArcRepository)
    private val output = object : ExamineCentralConflictOfTheme.OutputPort {
        override suspend fun centralConflictExamined(response: ExaminedCentralConflict) {
            examinedConflictOfTheme = response
        }
    }

    fun examineCentralConflictOfTheme(characterId: UUID? = null) = runBlocking {
        useCase.invoke(themeId.uuid, characterId, output)
    }

}