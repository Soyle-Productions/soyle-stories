package com.soyle.stories.character.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.CharacterNameCannotBeBlank
import com.soyle.stories.character.TestContext
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacterUseCase
import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.Character
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class RenameCharacterUnitTest {

	val characterId = UUID.randomUUID()
	val inputName = "Input Name"

	private lateinit var context: TestContext
	private var updatedCharacter: Character? = null
	private var result: Any? = null

	@BeforeEach
	fun clear() {
		result = null
		context = TestContext()
		updatedCharacter = null
	}

	@Test
	fun `character does not exist`() {
		givenNoCharacters()
		whenUseCaseIsExecuted()
		val result = result as CharacterDoesNotExist
		result.characterId.mustEqual(characterId)
	}

	@ParameterizedTest
	@ValueSource(strings = ["", "  ", "\r", "\n", "\r\n"])
	fun `name is blank`(inputName: String) {
		givenCharacterWithId(characterId = characterId)
		whenUseCaseIsExecuted(inputName = inputName)
		val result = result as CharacterNameCannotBeBlank
		result.characterId.mustEqual(characterId)
	}

	@Test
	fun `name is same as first name`() {
		givenCharacterWithId(characterId = characterId, andName = inputName)
		whenUseCaseIsExecuted()
		updatedCharacter.mustEqual(null) { "Character should not have been updated" }
		assertResultIsResponseModel()
	}

	@Test
	fun `name is valid`() {
		givenCharacterWithId(characterId = characterId)
		whenUseCaseIsExecuted()
		assertResultIsResponseModel()
	}

	@Test
	fun `character is persisted`() {
		givenCharacterWithId(characterId = characterId)
		whenUseCaseIsExecuted()
		val updatedCharacter = updatedCharacter!!
		updatedCharacter.name.mustEqual(inputName) { "Updated character name should be equal to input" }
	}



	fun givenNoCharacters() {
		context = TestContext(
		  updateCharacter = {
			  updatedCharacter = it
		  }
		)
	}

	fun givenCharacterWithId(characterId: UUID? = null, andName: String? = null) {
		context = TestContext(
		  initialCharacters = listOfNotNull(
			characterId?.let { Character(Character.Id(it), UUID.randomUUID(), andName ?: "Original Name") }
		  ),
		  updateCharacter = {
			  updatedCharacter = it
		  }
		)
	}

	private fun whenUseCaseIsExecuted(inputName: String = this.inputName) {
		val repo = context.characterRepository
		val useCase: RenameCharacter = RenameCharacterUseCase(repo)
		val output = object : RenameCharacter.OutputPort {
			override fun receiveRenameCharacterFailure(failure: CharacterException) {
				result = failure
			}

			override fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
				result = response
			}
		}

		runBlocking {
			useCase.invoke(characterId, inputName, output)
		}
	}

	private fun assertResultIsResponseModel() {
		val result = result as RenameCharacter.ResponseModel
		result.characterId.mustEqual(characterId)
		result.newName.mustEqual(inputName)
	}

}