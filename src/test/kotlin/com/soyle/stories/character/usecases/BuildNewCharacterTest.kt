package com.soyle.stories.character.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 1:03 PM
 */
class BuildNewCharacterTest {

	val providedName = "Character Name"
	val blankNames = listOf(
		"",
		"    ",
		"\r",
		"\n  ",
		"\r\n  \r"
	)

	fun given(addNewCharacter: (Character) -> Unit = {}): (String) -> Either<*, CharacterItem> {
		val repo = object : CharacterRepository {
			override suspend fun addNewCharacter(character: Character) = addNewCharacter.invoke(character)
			override suspend fun getCharacterById(characterId: Character.Id): Character? = null
			override suspend fun deleteCharacterWithId(characterId: Character.Id) = Unit
			override suspend fun updateCharacter(character: Character) {

			}
		}
		val useCase =
            BuildNewCharacterUseCase(
                Project.Id(UUID.randomUUID()), repo
            )
		val output = object : BuildNewCharacter.OutputPort {
			var result: Either<*, CharacterItem>? = null
			override fun receiveBuildNewCharacterFailure(failure: CharacterException) {
				result = failure.left()
			}

			override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
				result = response.right()
			}
		}
		return {
			runBlocking {
				useCase(it, output)
			}
			output.result!!
		}
	}

	val useCase = given()

	@Test
	fun `character should have provided name`() {
		val character: CharacterItem = (useCase(providedName) as Either.Right).b
		assertEquals(providedName, character.characterName)
	}

	@Test
	fun `character name cannot be blank`() {
		blankNames.forEach {
			val error = (useCase(it) as Either.Left).a
			assert(error is com.soyle.stories.character.CharacterNameCannotBeBlank)
		}
	}

	@Test
	fun `new character should be persisted`() {
		var persistedCharacter: Character? = null
		val useCase = given(addNewCharacter = {
			persistedCharacter = it
		})
		useCase(providedName)
		assertEquals(providedName, persistedCharacter!!.name)
	}

	@Test
	fun `output character should have id from created character`() {
		var persistedCharacter: Character? = null
		val useCase = given(addNewCharacter = {
			persistedCharacter = it
		})
		val (result) = useCase(providedName) as Either.Right
		assertEquals(persistedCharacter!!.id.uuid, result.characterId)
	}

}