package com.soyle.stories.character.usecases.buildNewCharacter

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.usecases.validateCharacterName
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 2:40 PM
 */
class BuildNewCharacterUseCase(
    private val projectId: Project.Id,
    private val characterRepository: CharacterRepository
) : BuildNewCharacter {

    override suspend fun invoke(name: String, outputPort: BuildNewCharacter.OutputPort) {
        val response = try { execute(name) }
        catch (e: CharacterException) {
            return outputPort.receiveBuildNewCharacterFailure(e)
        }
        outputPort.receiveBuildNewCharacterResponse(response)
    }

    private suspend fun execute(name: String): CharacterItem
    {
        validateCharacterName(name)

        val character = Character.buildNewCharacter(projectId, name)

        characterRepository.addNewCharacter(character)

        return CharacterItem(
          character.id.uuid,
          character.name
        )
    }
}