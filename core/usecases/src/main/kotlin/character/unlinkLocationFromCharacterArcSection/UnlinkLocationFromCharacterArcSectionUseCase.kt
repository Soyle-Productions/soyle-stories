package com.soyle.stories.usecase.character.unlinkLocationFromCharacterArcSection

import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterArcSectionDoesNotExist
import java.util.*

class UnlinkLocationFromCharacterArcSectionUseCase(
  private val characterArcRepository: CharacterArcRepository
) : UnlinkLocationFromCharacterArcSection {
	override suspend fun invoke(characterArcSectionId: UUID, output: UnlinkLocationFromCharacterArcSection.OutputPort) {
		val response = try {
			val characterArc = characterArcRepository.getCharacterArcContainingArcSection(CharacterArcSection.Id(characterArcSectionId))
			  ?: throw CharacterArcSectionDoesNotExist(characterArcSectionId)
			val characterArcSection = characterArc.arcSections.find { it.id.uuid == characterArcSectionId }
				?: throw CharacterArcSectionDoesNotExist(characterArcSectionId)

			if (characterArcSection.linkedLocation != null) {
				characterArcRepository.replaceCharacterArcs(characterArc.withArcSectionsMapped {
					if (it.id ==  characterArcSection.id) it.withoutLinkedLocation()
					else it
				})
			}

			UnlinkLocationFromCharacterArcSection.ResponseModel(characterArcSectionId)
		} catch (c: Exception) {
			return output.receiveUnlinkLocationFromCharacterArcSectionFailure(c)
		}
		output.receiveUnlinkLocationFromCharacterArcSectionResponse(response)
	}
}