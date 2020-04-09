package com.soyle.stories.characterarc.usecases.renameCharacterArc

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.characterarc.CharacterArcNameCannotBeBlank
import com.soyle.stories.characterarc.repositories.CharacterArcRepository
import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import java.util.*

class RenameCharacterArcUseCase(
  private val characterRepository: CharacterRepository,
  private val themeRepository: ThemeRepository,
  private val characterArcRepository: CharacterArcRepository
) : RenameCharacterArc {
	override suspend fun invoke(request: RenameCharacterArc.RequestModel, outputPort: RenameCharacterArc.OutputPort) {
		val response = try {
			renameCharacterArc(request)
		} catch (e: Exception) {
			return outputPort.receiveRenameCharacterArcFailure(e)
		}
		outputPort.receiveRenameCharacterArcResponse(response)
	}

	private suspend fun renameCharacterArc(request: RenameCharacterArc.RequestModel): RenameCharacterArc.ResponseModel {
		validateRequest(request)
		val characterArc = getCharacterArc(request)
		if (characterArc.name == request.name) return convertRequestToResponse(request)
		characterArcRepository.updateCharacterArc(characterArc.withNewName(request.name))
		return convertRequestToResponse(request)
	}

	private suspend fun getCharacterArc(request: RenameCharacterArc.RequestModel) =
	  characterArcRepository.getCharacterArcByCharacterAndThemeId(Character.Id(request.characterId), Theme.Id(request.themeId))
		?: throw CharacterArcDoesNotExist(request.characterId, request.themeId)

	private fun convertRequestToResponse(request: RenameCharacterArc.RequestModel): RenameCharacterArc.ResponseModel {
		return RenameCharacterArc.ResponseModel(request.characterId, request.themeId, request.name)
	}

	private suspend fun validateRequest(request: RenameCharacterArc.RequestModel) {
		validateName(request)
		validateCharacter(request)
		validateTheme(request)
	}

	private fun validateName(request: RenameCharacterArc.RequestModel) {
		if (request.name.isBlank()) throw CharacterArcNameCannotBeBlank(request.characterId, request.themeId)
	}

	private suspend fun validateCharacter(request: RenameCharacterArc.RequestModel) {
		characterRepository.getCharacterById(Character.Id(request.characterId))
		  ?: throw CharacterDoesNotExist(request.characterId)
	}

	private suspend fun validateTheme(request: RenameCharacterArc.RequestModel) {
		val theme = themeRepository.getThemeById(Theme.Id(request.themeId))
		  ?: throw ThemeDoesNotExist(request.themeId)

		if (! theme.containsCharacter(Character.Id(request.characterId))) throw CharacterNotInTheme(request.themeId, request.characterId)
	}
}