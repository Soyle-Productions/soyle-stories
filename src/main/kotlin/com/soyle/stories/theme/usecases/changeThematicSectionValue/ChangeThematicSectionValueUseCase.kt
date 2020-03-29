/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 4:48 PM
 */
package com.soyle.stories.theme.usecases.changeThematicSectionValue

import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import java.util.*

class ChangeThematicSectionValueUseCase(
    private val characterArcSectionRepository: CharacterArcSectionRepository
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
        val characterArcSection = getCharacterArcSectionById(thematicSectionId)
        val updatedArcSection = characterArcSection.changeValue(value)
        saveUpdate(updatedArcSection)
        return ChangeThematicSectionValue.ResponseModel(thematicSectionId, value)
    }

    private suspend fun getCharacterArcSectionById(thematicSectionId: UUID): CharacterArcSection {
        return characterArcSectionRepository
            .getCharacterArcSectionById(CharacterArcSection.Id(thematicSectionId))
            ?: throw com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist(thematicSectionId)
    }

    private suspend fun saveUpdate(updatedArcSection: CharacterArcSection) {
        characterArcSectionRepository.updateCharacterArcSection(updatedArcSection)
    }
}