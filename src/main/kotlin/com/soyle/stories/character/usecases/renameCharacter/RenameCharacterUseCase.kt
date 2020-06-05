package com.soyle.stories.character.usecases.renameCharacter

import arrow.core.identity
import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.repositories.ThemeRepository
import com.soyle.stories.character.usecases.validateCharacterName
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import java.util.*

class RenameCharacterUseCase(
  private val characterRepository: CharacterRepository,
  private val themeRepository: ThemeRepository
) : RenameCharacter {

	override suspend fun invoke(characterId: UUID, name: String, output: RenameCharacter.OutputPort) {
		val response = try {
			renameCharacter(characterId, name)
		} catch (c: CharacterException) {
			return output.receiveRenameCharacterFailure(c)
		}
		output.receiveRenameCharacterResponse(response)
	}

	private suspend fun renameCharacter(characterId: UUID, name: String): RenameCharacter.ResponseModel {
		val character = getCharacter(characterId)
		if (character.name == name) return RenameCharacter.ResponseModel(characterId, name, emptyList())
		val themes = getThemesWithCharacter(character.id)
		changeNameForAllInstancesOfCharacter(name, character, themes)
		return RenameCharacter.ResponseModel(characterId, name, themes.map { it.id.uuid })
	}

	private suspend fun getCharacter(characterId: UUID): Character {
		return characterRepository.getCharacterById(Character.Id(characterId)) ?:
		  throw CharacterDoesNotExist(characterId)
	}

	private suspend fun getThemesWithCharacter(characterId: Character.Id): List<Theme>
	{
		return themeRepository.getThemesWithCharacterIncluded(characterId)
	}

	private suspend fun changeNameForAllInstancesOfCharacter(name: String, character: Character, themes: List<Theme>) {
		validateCharacterName(name)
		characterRepository.updateCharacter(character.withName(name))
		renameCharacterInThemes(character, name)
	}

	private suspend fun renameCharacterInThemes(character: Character, name: String)
	{
		val themes = themeRepository.getThemesWithCharacterIncluded(character.id)

		if (themes.isEmpty()) return

		val updatedThemes = themes.map { theme ->
			theme.getIncludedCharacterById(character.id)?.let {
				theme.withCharacterRenamed(it, name).fold(
				  { throw it },
				  ::identity
				)
			} ?: theme
		}

		themeRepository.updateThemes(updatedThemes)

	}

}