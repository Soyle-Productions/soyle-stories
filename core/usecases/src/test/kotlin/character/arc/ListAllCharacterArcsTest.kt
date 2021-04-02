package com.soyle.stories.usecase.character.arc

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.media.Media
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.*
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ListAllCharacterArcsTest {

	private val projectId = Project.Id()

	private var result: Any? = null

	@Test
	fun `no characters`() {
		listAllCharacterArcs()
		result shouldBe ::emptyResult
	}

	@Test
	fun `characters without arcs`() {
		givenCharacters(count = 4)
		listAllCharacterArcs()
		result shouldBe responseModel(expectedCount = 4)
	}

	@Test
	fun `characters with arcs`() {
		givenCharacters(count = 3, arcsPerCharacter = 4)
		listAllCharacterArcs()
		result shouldBe responseModel(expectedCount = 3, expectedTotalArcCount = 12)
	}

	private val characterRepository = CharacterRepositoryDouble()
	private val characterArcRepository = CharacterArcRepositoryDouble()

	private fun givenCharacters(count: Int, arcsPerCharacter: Int = 0)
	{
		repeat(count) {
			val character = makeCharacter(projectId = projectId, media = Media.Id())
			characterRepository.characters[character.id] = character
			repeat(arcsPerCharacter) {
				val arc = CharacterArc.planNewCharacterArc(character.id, Theme.Id(), "Character Arc ${str()}")
				characterArcRepository.givenCharacterArc(arc)
			}
		}
	}

	private fun listAllCharacterArcs()
	{
		val useCase: ListAllCharacterArcs = ListAllCharacterArcsUseCase(characterRepository, characterArcRepository)
		val output = object : ListAllCharacterArcs.OutputPort {
			override suspend fun receiveCharacterArcList(response: CharacterArcsByCharacter) {
				result = response
			}
		}
		runBlocking {
			useCase.invoke(projectId.uuid, output)
		}
	}

	private fun emptyResult(actual: Any?) {
		actual as CharacterArcsByCharacter
		assertTrue(actual.characters.isEmpty())
	}

	private fun responseModel(expectedCount: Int, expectedTotalArcCount: Int = 0) = fun (actual: Any?) {
		actual as CharacterArcsByCharacter
		assertEquals(expectedCount, actual.characters.size)
		assertEquals(expectedTotalArcCount, actual.characters.flatMap { it.arcs }.size)
		assertCharactersHaveCorrectNames(actual.characters.map { it.character })
		assertCharactersHaveCorrectMediaIds(actual.characters.map { it.character })
		assertCharacterArcsHaveCorrectNames(actual.characters.flatMap { it.arcs })
	}

	private fun assertCharactersHaveCorrectNames(items: List<CharacterItem>) {
		items.forEach {
			assertEquals(
				characterRepository.characters[Character.Id(it.characterId)]!!.name.value,
				it.characterName
			)
		}
	}

	private fun assertCharactersHaveCorrectMediaIds(items: List<CharacterItem>) {
		items.forEach {
			assertEquals(
                characterRepository.characters[Character.Id(it.characterId)]!!.media?.uuid,
				it.mediaId
			)
		}
	}

	private fun assertCharacterArcsHaveCorrectNames(items: List<CharacterArcItem>) {
		runBlocking {
			items.forEach {
				val baseArc =
					characterArcRepository.getCharacterArcByCharacterAndThemeId(Character.Id(it.characterId), Theme.Id(it.themeId))
						?: throw error(it.characterArcName + " is missing from the repository.  CharacterId =${it.characterId}, ThemeId = ${it.themeId}")
				assertEquals(
					baseArc.name,
					it.characterArcName
				)
			}
		}
	}

}