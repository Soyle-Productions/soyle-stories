package com.soyle.stories.character.usecases

import arrow.core.identity
import com.soyle.stories.character.*
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacterUseCase
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class RenameCharacterUnitTest {

	val characterId = UUID.randomUUID()
	val themeId = UUID.randomUUID()
	val inputName = NonBlankString.create("Input Name")!!

	private lateinit var context: TestContext
	private var updatedCharacter: Character? = null
	private var updatedTheme: Theme? = null
	private var result: Any? = null

	@BeforeEach
	fun clear() {
		result = null
		context = TestContext()
		updatedCharacter = null
		updatedTheme = null
	}

	@Test
	fun `character does not exist`() {
		givenNoCharacters()
		whenUseCaseIsExecuted()
		val result = result as CharacterDoesNotExist
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

	@Test
	fun `theme with character`() {
		givenCharacterWithId(characterId = characterId, andThemeId = themeId, andThemeHasCharacter = true)
		whenUseCaseIsExecuted()
		assertThemeWasUpdated()
		assertResultHasAffectedThemes()
	}


	fun givenNoCharacters() {
		context = TestContext(
		  updateCharacter = {
			  updatedCharacter = it
		  },
		  updateThemes = {
			  updatedTheme = it.single()
		  }
		)
	}

	fun givenCharacterWithId(characterId: UUID? = null, andName: NonBlankString? = null, andThemeId: UUID? = null, andThemeHasCharacter: Boolean = false) {
		val character = characterId?.let { makeCharacter(Character.Id(it), Project.Id(), andName ?: NonBlankString.create("Original Name")!!) }
		context = TestContext(
		  initialCharacters = listOfNotNull(
			character
		  ),
		  initialThemes = listOfNotNull(
			andThemeId?.let { expectedId ->
				val theme = makeTheme(id = Theme.Id(expectedId))
				if (andThemeHasCharacter) {
					theme.withCharacterIncluded(character!!.id, character.name.value, character.media)
				}
				else theme
			}
		  ),
		  updateCharacter = {
			  updatedCharacter = it
		  },
		  updateThemes = {
			  updatedTheme = it.single()
		  }
		)
	}

	private fun whenUseCaseIsExecuted(inputName: NonBlankString = this.inputName) {
		val repo = context.characterRepository
		val useCase: RenameCharacter = RenameCharacterUseCase(repo, context.themeRepository)
		val output = object : RenameCharacter.OutputPort {
			override fun receiveRenameCharacterFailure(failure: CharacterException) {
				result = failure
			}

			override suspend fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
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
		result.newName.mustEqual(inputName.value)
	}

	private fun assertThemeWasUpdated() {
		val updatedTheme = updatedTheme!!
		updatedTheme.getIncludedCharacterById(Character.Id(characterId))!!.name.mustEqual(inputName.value)
	}

	private fun assertResultHasAffectedThemes() {
		val result = result as RenameCharacter.ResponseModel
		result.affectedThemeIds.contains(themeId).mustEqual(true) { "Affected theme ids in output must include theme id $themeId.  Only had ${result.affectedThemeIds}" }
	}

}