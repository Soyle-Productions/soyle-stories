package com.soyle.stories.character.characterComparison.controllers

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonController
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CharacterComparisonControllerUnitTest {

	val themeId = UUID.randomUUID()
	val characterId = UUID.randomUUID()

	var listAllCharacterArcsCalled: Boolean = false
	var compareCharactersCalled: Boolean = false
	var compareCharactersRequestedThemeId: UUID? = null
	var compareCharactersRequestedFocusCharacterId: UUID? = null

	@BeforeEach
	fun clear() {
		listAllCharacterArcsCalled = false
		compareCharactersCalled = false
		compareCharactersRequestedThemeId = null
		compareCharactersRequestedFocusCharacterId = null
	}

	@Test
	fun `character id is blank`() {
		val exception = assertThrows<IllegalArgumentException> {
			whenCalledWith("")
		}
		assertEquals("Character id cannot be blank", exception.localizedMessage)
	}

	@Test
	fun `character id is invalid uuid`() {
		val exception = assertThrows<IllegalArgumentException> {
			whenCalledWith("abcd")
		}
		assertEquals("Character id is invalid", exception.localizedMessage)
	}

	@Test
	fun `calls list all character arcs use case`() {
		whenCalledWith()
		assertTrue(listAllCharacterArcsCalled)
	}

	@Test
	fun `calls compare characters use case`() {
		whenCalledWith()
		assertTrue(compareCharactersCalled)
		assertEquals(themeId, compareCharactersRequestedThemeId)
		assertEquals(characterId, compareCharactersRequestedFocusCharacterId)
	}

	private fun whenCalledWith(characterId: String = this.characterId.toString()) {
		val controller = CharacterComparisonController(
		  themeId.toString(),
		  object : ListAllCharacterArcs {
			  override suspend fun invoke(outputPort: ListAllCharacterArcs.OutputPort) {
				  listAllCharacterArcsCalled = true
			  }
		  },
		  object : ListAllCharacterArcs.OutputPort {
			  override fun receiveCharacterArcList(response: ListAllCharacterArcs.ResponseModel) {

			  }
		  },
		  object : CompareCharacters {
			  override suspend fun invoke(themeId: UUID, focusCharacterId: UUID, outputPort: CompareCharacters.OutputPort) {
				  compareCharactersCalled = true
				  compareCharactersRequestedThemeId = themeId
				  compareCharactersRequestedFocusCharacterId = focusCharacterId
			  }
		  },
		  object : CompareCharacters.OutputPort {
			  override fun receiveCharacterComparison(response: CompareCharacters.ResponseModel) {

			  }

			  override fun receiveCompareCharactersFailure(error: ThemeException) {

			  }
		  }
		)
		runBlocking {
			controller.getCharacterComparison(characterId)
		}
	}

}