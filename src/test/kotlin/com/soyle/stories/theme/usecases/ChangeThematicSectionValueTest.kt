/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 4:46 PM
 */
package com.soyle.stories.theme.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.common.template
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValueUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ChangeThematicSectionValueTest {

	private fun given(characterArcSections: List<CharacterArcSection> = emptyList(), updateCharacterArc: (CharacterArc) -> Unit = {}): (UUID, String) -> Either<Exception, ChangeThematicSectionValue.ResponseModel> {
		val repo = CharacterArcRepositoryDouble(
			onUpdateCharacterArc = updateCharacterArc
		).apply {
			characterArcSections.groupBy { it.themeId to it.characterId }
				.map {
					it.value.fold(
						CharacterArc.planNewCharacterArc(it.key.second, it.key.first, "", CharacterArcTemplate(it.value.map { it.template }))
					) { arc, section ->
						arc.withArcSection(section)
					}
				}.forEach {
					givenCharacterArc(it)
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
				template("", false),
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
			var updatedCharacterArc: CharacterArc? = null
			val useCase = given(characterArcSections, updateCharacterArc = {
				updatedCharacterArc = it
			})
			useCase.invoke(thematicSectionUUID, newValue)
			updatedCharacterArc!!.arcSections.find { it.id.uuid == thematicSectionUUID }!!.run {
				assertEquals(newValue, value)
			}
		}

	}

}