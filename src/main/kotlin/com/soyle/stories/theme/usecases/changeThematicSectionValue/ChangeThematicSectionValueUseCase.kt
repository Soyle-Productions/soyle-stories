package com.soyle.stories.theme.usecases.changeThematicSectionValue

import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.theme.repositories.CharacterArcRepository
import java.util.*

class ChangeThematicSectionValueUseCase(
    private val characterArcRepository: CharacterArcRepository
) : ChangeThematicSectionValue {

    override suspend fun invoke(thematicSectionId: UUID, value: String, output: ChangeThematicSectionValue.OutputPort) {
        val response = try {
            changeThematicSectionValue(thematicSectionId, value)
        } catch (e: com.soyle.stories.characterarc.CharacterArcException) {
            return output.receiveChangeThematicSectionValueFailure(e)
        }
        output.receiveChangeThematicSectionValueResponse(response)
    }

    private suspend fun changeThematicSectionValue(
        thematicSectionId: UUID, value: String
    ): ChangeThematicSectionValue.ResponseModel {
        val characterArc = getCharacterArc(thematicSectionId)
        val updatedArc = characterArc.withArcSectionsMapped {
            if (it.id.uuid == thematicSectionId) it.changeValue(value)
            else it
        }
        saveUpdate(updatedArc)
        return ChangeThematicSectionValue.ResponseModel(thematicSectionId, value)
    }

    private suspend fun getCharacterArc(thematicSectionId: UUID): CharacterArc {
        return characterArcRepository
            .getCharacterArcContainingArcSection(CharacterArcSection.Id(thematicSectionId))
            ?: throw com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist(thematicSectionId)
    }

    private suspend fun saveUpdate(updatedArc: CharacterArc) {
        characterArcRepository.replaceCharacterArcs(updatedArc)
    }
}