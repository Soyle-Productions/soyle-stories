package com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection

import com.soyle.stories.characterarc.CharacterArcException
import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.theme.repositories.CharacterArcRepository
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
		} catch (c: CharacterArcException) {
			return output.receiveUnlinkLocationFromCharacterArcSectionFailure(c)
		}
		output.receiveUnlinkLocationFromCharacterArcSectionResponse(response)
	}
}