package com.soyle.stories.characterarc.usecases.renameCharacterArc

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.characterarc.CharacterArcNameCannotBeBlank
import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist

class RenameCharacterArcUseCase(
  private val characterRepository: CharacterRepository,
  private val themeRepository: ThemeRepository
) : RenameCharacterArc {

	override suspend fun invoke(request: RenameCharacterArc.RequestModel, outputPort: RenameCharacterArc.OutputPort) {
		val response = renameCharacterArc(request)
		outputPort.receiveRenameCharacterArcResponse(response)
	}

	private suspend fun renameCharacterArc(request: RenameCharacterArc.RequestModel): RenameCharacterArc.ResponseModel {
		validateRequest(request)
		val theme = getTheme(request)
		val characterArc = theme.getMajorCharacterById(Character.Id(request.characterId))?.characterArc
			?: throw CharacterArcDoesNotExist(request.characterId, request.themeId)
		if (characterArc.name == request.name) return convertRequestToResponse(request)
		themeRepository.updateTheme(theme.withCharacterArcRenamed(Character.Id(request.characterId), request.name))
		return convertRequestToResponse(request)
	}

	private fun convertRequestToResponse(request: RenameCharacterArc.RequestModel): RenameCharacterArc.ResponseModel {
		return RenameCharacterArc.ResponseModel(request.characterId, request.themeId, request.name)
	}

	private suspend fun validateRequest(request: RenameCharacterArc.RequestModel) {
		validateName(request)
		validateCharacter(request)
	}

	private fun validateName(request: RenameCharacterArc.RequestModel) {
		if (request.name.isBlank()) throw CharacterArcNameCannotBeBlank(request.characterId, request.themeId)
	}

	private suspend fun validateCharacter(request: RenameCharacterArc.RequestModel) {
		characterRepository.getCharacterById(Character.Id(request.characterId))
		  ?: throw CharacterDoesNotExist(request.characterId)
	}

	private suspend fun getTheme(request: RenameCharacterArc.RequestModel): Theme {
		val theme = themeRepository.getThemeById(Theme.Id(request.themeId))
		  ?: throw ThemeDoesNotExist(request.themeId)

		if (! theme.containsCharacter(Character.Id(request.characterId))) throw CharacterNotInTheme(request.themeId, request.characterId)

		return theme
	}
}