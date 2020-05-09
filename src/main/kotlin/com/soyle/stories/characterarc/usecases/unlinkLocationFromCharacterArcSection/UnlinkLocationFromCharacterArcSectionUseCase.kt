package com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection

import com.soyle.stories.characterarc.CharacterArcException
import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import java.util.*

class UnlinkLocationFromCharacterArcSectionUseCase(
  private val characterArcSectionRepository: CharacterArcSectionRepository
) : UnlinkLocationFromCharacterArcSection {
	override suspend fun invoke(characterArcSectionId: UUID, output: UnlinkLocationFromCharacterArcSection.OutputPort) {
		val response = try {
			val characterArcSection = characterArcSectionRepository.getCharacterArcSectionById(CharacterArcSection.Id(characterArcSectionId))
			  ?: throw CharacterArcSectionDoesNotExist(characterArcSectionId)

			if (characterArcSection.linkedLocation != null) {
				characterArcSectionRepository.updateCharacterArcSection(characterArcSection.withoutLinkedLocation())
			}

			UnlinkLocationFromCharacterArcSection.ResponseModel(characterArcSectionId)
		} catch (c: CharacterArcException) {
			return output.receiveUnlinkLocationFromCharacterArcSectionFailure(c)
		}
		output.receiveUnlinkLocationFromCharacterArcSectionResponse(response)
	}
}