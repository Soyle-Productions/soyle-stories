package com.soyle.stories.usecase.character.renameCharacterArc

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcDoesNotExist
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository

class RenameCharacterArcUseCase(
  private val characterRepository: CharacterRepository,
  private val characterArcRepository: CharacterArcRepository
) : RenameCharacterArc {

	override suspend fun invoke(request: RenameCharacterArc.RequestModel, outputPort: RenameCharacterArc.OutputPort) {
		val response = renameCharacterArc(request)
		outputPort.receiveRenameCharacterArcResponse(response)
	}

	private suspend fun renameCharacterArc(request: RenameCharacterArc.RequestModel): RenameCharacterArc.ResponseModel {
		validateRequest(request)
		val characterArc = characterArcRepository.getCharacterArcByCharacterAndThemeId(Character.Id(request.characterId), Theme.Id(request.themeId))
			?: throw CharacterArcDoesNotExist(request.characterId, request.themeId)
		if (characterArc.name == request.name.value) return convertRequestToResponse(request)
		characterArcRepository.replaceCharacterArcs(characterArc.withNewName(request.name.value))
		return convertRequestToResponse(request)
	}

	private fun convertRequestToResponse(request: RenameCharacterArc.RequestModel): RenameCharacterArc.ResponseModel {
		return RenameCharacterArc.ResponseModel(request.characterId, request.themeId, request.name.value)
	}

	private suspend fun validateRequest(request: RenameCharacterArc.RequestModel) {
		validateCharacter(request)
	}

	private suspend fun validateCharacter(request: RenameCharacterArc.RequestModel) {
		characterRepository.getCharacterById(Character.Id(request.characterId))
		  ?: throw CharacterDoesNotExist(request.characterId)
	}
}