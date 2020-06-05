package com.soyle.stories.characterarc.usecases

import arrow.core.identity
import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.characterarc.CharacterArcNameCannotBeBlank
import com.soyle.stories.characterarc.TestContext
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArcUseCase
import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.*
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.takeNoteOfTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class RenameCharacterArcUnitTest {

	private val characterId = UUID.randomUUID()
	private val themeId = UUID.randomUUID()
	private val characterArcName = "Original Name"

	private var inputName = "New Name"
	private lateinit var context: TestContext
	private var result: Any? = null
	private var updatedCharacterArc: CharacterArc? = null

	@BeforeEach
	fun clear() {
		inputName = "New Name"
		result = null
		context = TestContext()
		updatedCharacterArc = null
	}

	@Test
	fun `character does not exist`() {
		givenNoCharacters()
		whenUseCaseIsExecuted()
		val result = result as CharacterDoesNotExist
		result.characterId.mustEqual(characterId)
	}

	@Test
	fun `theme does not exist`() {
		givenNoThemes()
		whenUseCaseIsExecuted()
		val result = result as ThemeDoesNotExist
		result.themeId.mustEqual(themeId)
	}

	@Test
	fun `character not in theme`() {
		given(characterWithId = characterId, andThemeWithId = themeId)
		whenUseCaseIsExecuted()
		val result = result as CharacterNotInTheme
		result.characterId.mustEqual(characterId)
		result.themeId.mustEqual(themeId)
	}

	@Test
	fun `character arc does not exist`() {
		given(characterWithId = characterId, andThemeWithId = themeId, andThemeHasCharacter = true)
		whenUseCaseIsExecuted()
		val result = result as CharacterArcDoesNotExist
		result.characterId.mustEqual(characterId)
		result.themeId.mustEqual(themeId)
	}

	@ParameterizedTest
	@ValueSource(strings = ["", " ", "\r", "\n", "\r\n"])
	fun `name is blank`(inputName: String) {
		given(characterWithId = characterId, andThemeWithId = themeId, andThemeHasCharacter = true, andCharacterIsMajorCharacter = true)
		this.inputName = inputName
		whenUseCaseIsExecuted()
		val result = result as CharacterArcNameCannotBeBlank
		result.characterId.mustEqual(characterId)
		result.themeId.mustEqual(themeId)
	}

	@Test
	fun `same name`() {
		given(characterWithId = characterId, andThemeWithId = themeId, andThemeHasCharacter = true, andCharacterIsMajorCharacter = true)
		inputName = characterArcName
		whenUseCaseIsExecuted()
		assertResultIsResponseModel()
		updatedCharacterArc.mustEqual(null) { "Character arc should not have been updated" }
	}

	@Test
	fun `valid name`() {
		given(characterWithId = characterId, andThemeWithId = themeId, andThemeHasCharacter = true, andCharacterIsMajorCharacter = true)
		whenUseCaseIsExecuted()
		assertResultIsResponseModel()
		assertCharacterArcWasUpdated()
	}

	private fun givenNoCharacters() = given()
	private fun givenNoThemes() = given(characterWithId = characterId)

	private fun given(characterWithId: UUID? = null, andThemeWithId: UUID? = null, andThemeHasCharacter: Boolean = false, andCharacterIsMajorCharacter: Boolean = false) {
		val character = characterWithId?.let { Character(Character.Id(characterWithId), Project.Id(), "Bob") }
		context = TestContext(
		  initialCharacters = listOfNotNull(
			character
		  ),
		  initialThemes = listOfNotNull(
			andThemeWithId?.let {
				val theme = takeNoteOfTheme(andThemeWithId)
				if (andThemeHasCharacter) theme.includeCharacter(character!!, emptyList()).fold({ throw it }, ::identity)
				else theme
			}
		  ),
		  initialCharacterArcs = listOfNotNull(
			Unit.takeIf { andCharacterIsMajorCharacter }?.let {
				CharacterArc(Character.Id(characterWithId!!), CharacterArcTemplate.default(), Theme.Id(andThemeWithId!!), characterArcName)
			}
		  ),
		  updateCharacterArc = {
			  updatedCharacterArc = it
		  }
		)
	}

	private fun whenUseCaseIsExecuted() {
		val useCase: RenameCharacterArc = RenameCharacterArcUseCase(context.characterRepository, context.themeRepository, context.characterArcRepository)
		val output = object : RenameCharacterArc.OutputPort {
			override fun receiveRenameCharacterArcFailure(failure: Exception) {
				result = failure
			}

			override fun receiveRenameCharacterArcResponse(response: RenameCharacterArc.ResponseModel) {
				result = response
			}
		}

		runBlocking {
			useCase.invoke(RenameCharacterArc.RequestModel(characterId, themeId, inputName), output)
		}
	}

	private fun assertResultIsResponseModel() {
		val result = result as RenameCharacterArc.ResponseModel
		result.characterId.mustEqual(characterId)
		result.themeId.mustEqual(themeId)
		result.newName.mustEqual(inputName)
	}

	private fun assertCharacterArcWasUpdated() {
		val updatedCharacterArc = updatedCharacterArc!!
		updatedCharacterArc.name.mustEqual(inputName)
	}

}