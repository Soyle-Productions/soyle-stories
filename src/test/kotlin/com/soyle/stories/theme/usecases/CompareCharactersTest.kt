package com.soyle.stories.theme.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.entities.theme.MinorCharacter
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharactersUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/24/2020
 * Time: 4:54 PM
 */
class CompareCharactersTest {

	fun given(themes: List<Theme>): (UUID, UUID) -> Either<ThemeException, CompareCharacters.ResponseModel> {
		val context = setupContext(
			initialThemes = themes,
			initialCharacterArcSections = themes.flatMap { it.characters.flatMap { it.thematicSections.map { it.asCharacterArcSection(null) } } }
		)
		return { themeId, focusCharacterId ->
			val outputPort = object : CompareCharacters.OutputPort {
				var result: Either<ThemeException, CompareCharacters.ResponseModel>? = null
				override fun receiveCharacterComparison(response: CompareCharacters.ResponseModel) {
					result = response.right()
				}

				override fun receiveCompareCharactersFailure(error: ThemeException) {
					result = error.left()
				}
			}
			runBlocking {
				CompareCharactersUseCase(context).invoke(themeId, focusCharacterId, outputPort)
			}
			if (outputPort.result == null) error("No output received")
			outputPort.result!!
		}
	}

	fun ((UUID, UUID) -> Either<ThemeException, CompareCharacters.ResponseModel>).testWith(themeId: UUID, focusCharacterId: UUID) =
		this.invoke(themeId, focusCharacterId)

	@Test
	fun `non existent theme should output failure`() {
		val themes = listOf<Theme>()
		val themeId = UUID.randomUUID()
		val focusCharacterId = UUID.randomUUID()
		val result = given(themes).testWith(themeId, focusCharacterId)
		result as Either.Left
		assert(result.a is ThemeDoesNotExist)
	}

	@Test
	fun `character not in theme should output failure`() {
		val focusCharacterId = UUID.randomUUID()
		val (theme) = Theme.takeNoteOf(Project.Id(), "") as Either.Right
		val themes = listOf(theme)
		val themeId = theme.id.uuid
		val result = given(themes).testWith(themeId, focusCharacterId)
		result as Either.Left
		assert(result.a is CharacterNotInTheme)
	}

	@Test
	fun `character must be major character`() {
		val focusCharacterId = UUID.randomUUID()
		val (theme) = Theme.takeNoteOf(Project.Id(), "")
			.flatMap { it.includeCharacter(Character(Character.Id(focusCharacterId), Project.Id(), "Bob")) } as Either.Right
		val themes = listOf(theme)
		val themeId = theme.id.uuid
		val result = given(themes).testWith(themeId, focusCharacterId)
		result as Either.Left
	}


	@Nested
	inner class GivenSuccessfulExecution {

		val majorCharacters = List(3) { Character.Id(UUID.randomUUID()) }.map {
            Character(
                it,
			  Project.Id(),
                it.uuid.toString()
            )
        }
		val minorCharacters = List(3) { Character.Id(UUID.randomUUID()) }.map {
            Character(
                it,
			  Project.Id(),
                it.uuid.toString()
            )
        }
		val theme: Theme
		val result: CompareCharacters.ResponseModel

		val centralMoralQuestion = "I'm the central moral question"

		val majorCharacter1Attack = "Dibble"
		val majorCharacter2Attack = "Bibble"
		val minorCharacter0Attack = "Bubble"

		val majorCharacter2Similarity = "Same"
		val minorCharacter1Similarity = "Same same"
		val minorCharacter2Similarity = "Similarity"

		init {
			val (themeTemp) = Theme.takeNoteOf(Project.Id(), centralMoralQuestion)
				.flatMap { it.includeCharacter(majorCharacters[0]) }
				.flatMap { it.includeCharacter(majorCharacters[1]) }
				.flatMap { it.includeCharacter(majorCharacters[2]) }
				.flatMap { it.includeCharacter(minorCharacters[0]) }
				.flatMap { it.includeCharacter(minorCharacters[1]) }
				.flatMap { it.includeCharacter(minorCharacters[2]) }
				.flatMap { it.promoteCharacter(it.getMinorCharacterById(majorCharacters[0].id) as MinorCharacter) }
				.flatMap { it.promoteCharacter(it.getMinorCharacterById(majorCharacters[1].id) as MinorCharacter) }
				.flatMap { it.promoteCharacter(it.getMinorCharacterById(majorCharacters[2].id) as MinorCharacter) }
				.flatMap { it.applyStoryFunction(it.getMajorCharacterById(majorCharacters[0].id) as MajorCharacter, majorCharacters[1].id, StoryFunction.Antagonist) }
				.flatMap { it.applyStoryFunction(it.getMajorCharacterById(majorCharacters[0].id) as MajorCharacter, majorCharacters[1].id, StoryFunction.Ally) }
				.flatMap { it.applyStoryFunction(it.getMajorCharacterById(majorCharacters[0].id) as MajorCharacter, majorCharacters[2].id, StoryFunction.FakeAllyAntagonist) }
				.flatMap { it.applyStoryFunction(it.getMajorCharacterById(majorCharacters[0].id) as MajorCharacter, minorCharacters[0].id, StoryFunction.FakeAntagonistAlly) }
				.flatMap { it.applyStoryFunction(it.getMajorCharacterById(majorCharacters[0].id) as MajorCharacter, minorCharacters[1].id, StoryFunction.Subplot) }

				.flatMap { it.changeAttack(it.getMajorCharacterById(majorCharacters[0].id) as MajorCharacter, majorCharacters[1].id, majorCharacter1Attack) }
				.flatMap { it.changeAttack(it.getMajorCharacterById(majorCharacters[0].id) as MajorCharacter, majorCharacters[2].id, majorCharacter2Attack) }
				.flatMap { it.changeAttack(it.getMajorCharacterById(majorCharacters[0].id) as MajorCharacter, minorCharacters[0].id, minorCharacter0Attack) }

				.flatMap { it.changeSimilarities(majorCharacters[0].id, majorCharacters[2].id, majorCharacter2Similarity) }
				.flatMap { it.changeSimilarities(majorCharacters[0].id, minorCharacters[1].id, minorCharacter1Similarity) }
				.flatMap { it.changeSimilarities(majorCharacters[0].id, minorCharacters[2].id, minorCharacter2Similarity) }

			  as Either.Right
			theme = themeTemp

			val (output) = given(listOf(theme)).testWith(theme.id.uuid, majorCharacters[0].id.uuid) as Either.Right
			result = output
		}

		@Test
		fun `output should list all major characters`() {
			assertEquals(majorCharacters.size, result.majorCharacterIds.size)
			majorCharacters.forEach {
				assertEquals(
					// using the uuid.toString to set the names.  So the names in the output should be the id's .toString
					it.id.uuid.toString(),
					result.characterSummaries.forceGetById(it.id.uuid).name
				)
			}
		}

		@Test
		fun `should have requested character as focus`() {
			assertEquals(majorCharacters[0].id.uuid, result.focusedCharacterId)
		}

		@Test
		fun `should output central moral question`() {
			assertEquals(centralMoralQuestion, result.centralQuestion)
		}

		@Test
		fun `story functions should come from characters perspective`() {
			fun assertStoryFunctionsMatch(characterId: Character.Id, vararg resultFunctions: CompareCharacters.StoryFunction) {
				assertEquals(
					resultFunctions.toSet(),
					result.characterSummaries.forceGetById(characterId.uuid).storyFunctions.toSet()
				)
			}
			assertStoryFunctionsMatch(majorCharacters[0].id, CompareCharacters.StoryFunction.Hero)
			assertStoryFunctionsMatch(majorCharacters[1].id, CompareCharacters.StoryFunction.Antagonist, CompareCharacters.StoryFunction.Ally)
			assertStoryFunctionsMatch(majorCharacters[2].id, CompareCharacters.StoryFunction.FakeAllyAntagonist)
			assertStoryFunctionsMatch(minorCharacters[0].id, CompareCharacters.StoryFunction.FakeAntagonistAlly)
			assertStoryFunctionsMatch(minorCharacters[1].id, CompareCharacters.StoryFunction.Subplot)
			assertStoryFunctionsMatch(minorCharacters[2].id)
		}

		@Test
		fun `should output archetypes for every character in theme`() {
			(majorCharacters + minorCharacters).forEach {
				assertEquals(
					theme.getIncludedCharacterById(it.id)!!.archetype,
					result.characterSummaries.forceGetById(it.id.uuid).archetypes
				)
			}
		}

		@Test
		fun `should output variations on moral for every character in theme`() {
			(majorCharacters + minorCharacters).forEach {
				assertEquals(
					theme.getIncludedCharacterById(it.id)!!.variationOnMoral,
					result.characterSummaries.forceGetById(it.id.uuid).variationOnMoral
				)
			}
		}

		@Test
		fun `should output attack on hero's weakness for every character in theme`() {
			fun assertAttackMatches(characterId: Character.Id, expectedAttack: String) {
				assertEquals(
					expectedAttack,
					result.characterSummaries.forceGetById(characterId.uuid).attackAgainstHero
				)
			}
			assertAttackMatches(majorCharacters[0].id, "")
			assertAttackMatches(majorCharacters[1].id, majorCharacter1Attack)
			assertAttackMatches(majorCharacters[2].id, majorCharacter2Attack)
			assertAttackMatches(minorCharacters[0].id, minorCharacter0Attack)
			assertAttackMatches(minorCharacters[1].id, "")
			assertAttackMatches(minorCharacters[2].id, "")
		}

		@Test
		fun `should output similarities between hero and every other character in theme`() {
			fun assertSimilaritiesMatch(characterId: Character.Id, expectedSimilarities: String) {
				assertEquals(
					expectedSimilarities,
					result.characterSummaries.forceGetById(characterId.uuid).similaritiesToHero
				)
			}
			assertSimilaritiesMatch(majorCharacters[0].id, "")
			assertSimilaritiesMatch(majorCharacters[1].id, "")
			assertSimilaritiesMatch(majorCharacters[2].id, majorCharacter2Similarity)
			assertSimilaritiesMatch(minorCharacters[0].id, "")
			assertSimilaritiesMatch(minorCharacters[1].id, minorCharacter1Similarity)
			assertSimilaritiesMatch(minorCharacters[2].id, minorCharacter2Similarity)
		}

		@Test
		fun `should output all thematic template sections`() {
			assertEquals(theme.thematicTemplate.sections.size, result.comparisonSections.size)
			val comparisonSections = result.comparisonSections.toSet()
			theme.thematicTemplate.sections.forEach {
				assert(comparisonSections.contains(it.name))
			}
		}

		@Test
		fun `should output thematic sections for each character`() {
			result.characterSummaries.values.forEach {
				assertEquals(theme.thematicTemplate.sections.size, it.comparisonSections.size)
				val characterInTheme = theme.getIncludedCharacterById(Character.Id(it.id))!!
				it.comparisonSections.forEachIndexed { index, value ->
					assertEquals(characterInTheme.thematicSections[index].characterArcSectionId.uuid, value.first)
				}
			}
		}

		@Test
		fun `each comp section should output section id`() {
			result.characterSummaries.values.forEach {
				val characterInTheme = theme.getIncludedCharacterById(Character.Id(it.id))!!
				it.comparisonSections.forEachIndexed { index, (uuid, _) ->
					assertEquals(
						characterInTheme.thematicSections[index].characterArcSectionId.uuid,
						uuid
					)
				}
			}
		}

	}
}