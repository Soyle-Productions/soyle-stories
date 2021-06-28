package com.soyle.stories.usecase.theme.changeThematicSectionValue

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterArcSectionDoesNotExist
import java.util.*

class ChangeThematicSectionValueUseCase(
    private val characterArcRepository: CharacterArcRepository
) : ChangeThematicSectionValue {

    override suspend fun invoke(thematicSectionId: UUID, value: String, output: ChangeThematicSectionValue.OutputPort) {
        val response = try {
            changeThematicSectionValue(thematicSectionId, value)
        } catch (e: Exception) {
            return output.receiveChangeThematicSectionValueFailure(e)
        }
        output.receiveChangeThematicSectionValueResponse(response)
    }

    private suspend fun changeThematicSectionValue(
        thematicSectionId: UUID, value: String
    ): ChangeThematicSectionValue.ResponseModel {
        val characterArc = getCharacterArc(thematicSectionId)
        val updatedArc = characterArc.withArcSectionsMapped {
            if (it.id.uuid == thematicSectionId) it.withValue(value)
            else it
        }
        saveUpdate(updatedArc)
        return ChangeThematicSectionValue.ResponseModel(thematicSectionId, value)
    }

    private suspend fun getCharacterArc(thematicSectionId: UUID): CharacterArc {
        return characterArcRepository
            .getCharacterArcContainingArcSection(CharacterArcSection.Id(thematicSectionId))
            ?: throw CharacterArcSectionDoesNotExist(thematicSectionId)
    }

    private suspend fun saveUpdate(updatedArc: CharacterArc) {
        characterArcRepository.replaceCharacterArcs(updatedArc)
    }
}