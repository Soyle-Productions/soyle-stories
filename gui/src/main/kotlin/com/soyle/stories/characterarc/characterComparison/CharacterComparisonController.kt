/**
 * Created by Brendan
 * Date: 3/3/2020
 * Time: 8:35 AM
 */
package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters
import java.util.*

class CharacterComparisonController(
  themeId: String,
  private val listAllCharacterArcs: ListAllCharacterArcs,
  private val listAllCharacterArcsOutputPort: ListAllCharacterArcs.OutputPort,
  private val compareCharacters: CompareCharacters,
  private val compareCharactersOutputPort: CompareCharacters.OutputPort
) {

	private val themeId: UUID = UUID.fromString(themeId)

	suspend fun getCharacterComparison(characterId: String) {
		val characterId = prepareCharacterId(characterId)
		listAllCharacterArcs.invoke(listAllCharacterArcsOutputPort)
		compareCharacters.invoke(
		  themeId,
		  characterId,
		  compareCharactersOutputPort
		)
	}

	private fun prepareCharacterId(characterId: String): UUID {
		if (characterId.isBlank()) throw IllegalArgumentException("Character id cannot be blank")
		return try {
			UUID.fromString(characterId)
		} catch (i: IllegalArgumentException) {
			throw IllegalArgumentException("Character id is invalid")
		}
	}
}