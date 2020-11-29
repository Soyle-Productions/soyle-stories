package com.soyle.stories.characterarc.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.characterarc.CharacterArcNameCannotBeBlank
import com.soyle.stories.characterarc.TestContext
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArcUseCase
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.nonBlankStr
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.takeNoteOfTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class RenameCharacterArcUnitTest {

	private val characterId = UUID.randomUUID()
	private val themeId = UUID.randomUUID()
	private val characterArcName = "Original Name"

	private var inputName = "New Name"
	private lateinit var context: TestContext
	private var updatedArc: CharacterArc? = null
	private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedArc::set)
	private var result: RenameCharacterArc.ResponseModel? = null

	@BeforeEach
	fun clear() {
		inputName = "New Name"
		result = null
		context = TestContext()
	}

	@Test
	fun `character does not exist`() {
		givenNoCharacters()
		val result = assertThrows<CharacterDoesNotExist> {
			whenUseCaseIsExecuted()
		}
		result.characterId.mustEqual(characterId)
	}

	@Test
	fun `character arc does not exist`() {
		given(characterWithId = characterId, andThemeWithId = themeId, andThemeHasCharacter = true)
		val result = assertThrows<CharacterArcDoesNotExist> {
			whenUseCaseIsExecuted()
		}
		result.characterId.mustEqual(characterId)
		result.themeId.mustEqual(themeId)
	}

	@ParameterizedTest
	@ValueSource(strings = ["", " ", "\r", "\n", "\r\n"])
	fun `name is blank`(inputName: String) {
		given(characterWithId = characterId, andThemeWithId = themeId, andThemeHasCharacter = true, andCharacterIsMajorCharacter = true)
		this.inputName = inputName
		val result = assertThrows<CharacterArcNameCannotBeBlank> {
			whenUseCaseIsExecuted()
		}
		result.characterId.mustEqual(characterId)
		result.themeId.mustEqual(themeId)
	}

	@Test
	fun `same name`() {
		given(characterWithId = characterId, andThemeWithId = themeId, andThemeHasCharacter = true, andCharacterIsMajorCharacter = true)
		inputName = characterArcName
		whenUseCaseIsExecuted()
		assertResultIsResponseModel()
		updatedArc.mustEqual(null) { "Character Arc should not have been updated" }
	}

	@Test
	fun `valid name`() {
		given(characterWithId = characterId, andThemeWithId = themeId, andThemeHasCharacter = true, andCharacterIsMajorCharacter = true)
		whenUseCaseIsExecuted()
		assertResultIsResponseModel()
		assertThemeHasCharacterWithRenamedCharacterArc()
	}

	private fun givenNoCharacters() = given()
	private fun givenNoThemes() = given(characterWithId = characterId)

	private fun given(characterWithId: UUID? = null, andThemeWithId: UUID? = null, andThemeHasCharacter: Boolean = false, andCharacterIsMajorCharacter: Boolean = false) {
		val character = characterWithId?.let { makeCharacter(Character.Id(characterWithId), Project.Id(), nonBlankStr()) }
		context = TestContext(
		  initialCharacters = listOfNotNull(
			character
		  ),
		  initialThemes = listOfNotNull(
			andThemeWithId?.let {
				val theme = makeTheme(Theme.Id(andThemeWithId), name = characterArcName)
				if (andThemeHasCharacter) {
					theme.withCharacterIncluded(character!!.id, character.name.value, character.media).let {
						if (andCharacterIsMajorCharacter) {
							characterArcRepository.givenCharacterArc(CharacterArc.planNewCharacterArc(character.id, it.id, it.name))
							it.withCharacterPromoted(character.id)
						}
						else it
					}
				}
				else theme
			}
		  )
		)
	}

	private fun whenUseCaseIsExecuted() {
		val useCase: RenameCharacterArc = RenameCharacterArcUseCase(context.characterRepository, characterArcRepository)
		val output = object : RenameCharacterArc.OutputPort {
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

	private fun assertThemeHasCharacterWithRenamedCharacterArc() {
		val updatedArc = updatedArc!!
		updatedArc.name.mustEqual(inputName)
	}

}