/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 4:46 PM
 */
package com.soyle.stories.theme.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValueUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ChangeThematicSectionValueTest {

	private fun given(characterArcSections: List<CharacterArcSection> = emptyList(), updateCharacterArcSection: (CharacterArcSection) -> Unit = {}): (UUID, String) -> Either<Exception, ChangeThematicSectionValue.ResponseModel> {
		val repo = object : CharacterArcSectionRepository {
			override suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection? =
				characterArcSections.find { it.id == characterArcSectionId }

			override suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection> {
				TODO("Not yet implemented")
			}

			override suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection) {
				updateCharacterArcSection.invoke(characterArcSection)
			}

			override suspend fun removeArcSections(sections: List<CharacterArcSection>) {

			}

			override suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>) {

			}

			override suspend fun getCharacterArcSectionsForCharacterInTheme(
				characterId: Character.Id,
				themeId: Theme.Id
			): List<CharacterArcSection> {
				TODO("Not yet implemented")
			}

			override suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection> {
				TODO("Not yet implemented")
			}

			override suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection> {
				TODO("Not yet implemented")
			}
		}
		val useCase: ChangeThematicSectionValue = ChangeThematicSectionValueUseCase(repo)
		val output = object : ChangeThematicSectionValue.OutputPort {
			var result: Either<Exception, ChangeThematicSectionValue.ResponseModel>? = null
			override fun receiveChangeThematicSectionValueFailure(failure: Exception) {
				result = failure.left()
			}

			override fun receiveChangeThematicSectionValueResponse(response: ChangeThematicSectionValue.ResponseModel) {
				result = response.right()
			}
		}
		return { thematicSectionUUID, newValue ->
			runBlocking {
				useCase.invoke(thematicSectionUUID, newValue, output)
			}
			output.result!!
		}
	}

	val thematicSectionUUID = UUID.randomUUID()
	val newValue = "I'm thematic!"

	@Nested
	inner class GivenCharacterArcDoesNotExist {

		val useCase = given()

		@Test
		fun `should output error`() {
			val (result) = useCase.invoke(thematicSectionUUID, newValue) as Either.Left
			result as com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
			assertEquals(thematicSectionUUID, result.characterArcSectionId)
		}

	}

	@Nested
	inner class GivenCharacterArcSectionExists {

		val characterArcSections = listOf(
			CharacterArcSection(
				CharacterArcSection.Id(thematicSectionUUID),
				Character.Id(UUID.randomUUID()),
				Theme.Id(UUID.randomUUID()),
				CharacterArcTemplateSection(
					CharacterArcTemplateSection.Id(
						UUID.randomUUID()
					), "", false
				),
			  null,
				""
			)
		)
		val useCase = given(characterArcSections)

		@Test
		fun `output should include provided thematic section id`() {
			val (result) = useCase.invoke(thematicSectionUUID, newValue) as Either.Right
			assertEquals(thematicSectionUUID, result.thematicSectionId)
		}

		@Test
		fun `output should include provided value`() {
			val (result) = useCase.invoke(thematicSectionUUID, newValue) as Either.Right
			assertEquals(newValue, result.newValue)
		}

		@Test
		fun `new value should be persisted`() {
			var updatedCharacterArcSection: CharacterArcSection? = null
			val useCase = given(characterArcSections, updateCharacterArcSection = {
				updatedCharacterArcSection = it
			})
			useCase.invoke(thematicSectionUUID, newValue)
			assertEquals(newValue, updatedCharacterArcSection!!.value)
		}

	}

}