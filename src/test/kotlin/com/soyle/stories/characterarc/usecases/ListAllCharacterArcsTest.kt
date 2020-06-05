package com.soyle.stories.characterarc.usecases

import arrow.core.Either
import arrow.core.right
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/25/2020
 * Time: 5:34 PM
 */
class ListAllCharacterArcsTest {

	val characters = List(5) {
		Character.buildNewCharacter(Project.Id(), UUID.randomUUID().toString())
	}
	val characterArcs = List(8) {
		val characterId = characters.random().id
		(CharacterArc.planNewCharacterArc(characterId, Theme.Id(UUID.randomUUID()), characterId.uuid.toString() + it) as Either.Right).b
	}

	fun given(characters: List<Character>, characterArcs: List<CharacterArc>): () -> Either<*, com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs.ResponseModel> {
		val repo = object : com.soyle.stories.characterarc.repositories.CharacterRepository,
            com.soyle.stories.characterarc.repositories.CharacterArcRepository {
			override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> = characters
			override suspend fun getCharacterById(characterId: Character.Id): Character? = null
			override suspend fun listAllCharacterArcsInProject(projectId: Project.Id): List<CharacterArc> = characterArcs
			override suspend fun addNewCharacterArc(characterArc: CharacterArc) = Unit
			override suspend fun getCharacterArcByCharacterAndThemeId(
                characterId: Character.Id,
                themeId: Theme.Id
			): CharacterArc? = null

			override suspend fun updateCharacterArc(characterArc: CharacterArc) {

			}
		}
		val output = object : com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs.OutputPort {
			var result: Either<*, com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs.ResponseModel>? = null
			override fun receiveCharacterArcList(response: com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs.ResponseModel) {
				result = response.right()
			}
		}
		val useCase =
            com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcsUseCase(
                UUID.randomUUID(),
                repo,
                repo
            )
		return {
			runBlocking {
				useCase(output)
				if (output.result == null) error("Output not received")
				output.result!!
			}
		}
	}

	@Nested
	inner class GivenNoCharacters {

		val useCase = given(emptyList(), emptyList())

		@Test
		fun `output should be empty`() {
			val (result) = useCase() as Either.Right
			assert(result.characters.isEmpty())
		}

	}

	@Nested
	inner class GivenNoCharacterArcs {

		val useCase = given(characters, emptyList())

		@Test
		fun `all characters should be in output`() {
			val(result) = useCase() as Either.Right
			assertEquals(characters.size, result.characters.size)
		}

		@Test
		fun `each character in output should be empty`() {
			val (result) = useCase() as Either.Right
			result.characters.values.forEach {
				assert(it.isEmpty())
			}
		}

	}

	@Nested
	inner class GivenCharacterArcs {

		val useCase = given(characters, characterArcs)

		@Test
		fun `character arcs are grouped by character`() {
			val (result) = useCase() as Either.Right
			val resultGroups = result.characters.mapKeys { it.key.characterId }
			val expectedGroups = characterArcs.groupBy { it.characterId }
			expectedGroups.forEach {
				assertEquals(it.value.size, resultGroups.getValue(it.key.uuid).size)
			}
		}

		@Test
		fun `should output character arc names`() {
			val (result) = useCase() as Either.Right
			val characterArcsByIds = characterArcs.associateBy { it.characterId.uuid to it.themeId.uuid }
			result.characters.values.flatten().forEach {
				assertEquals(characterArcsByIds.getValue(it.characterId to it.themeId).name, it.characterArcName)
			}
		}

	}

}