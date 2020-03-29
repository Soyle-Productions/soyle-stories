package com.soyle.stories.character.usecases.buildNewCharacter

import arrow.core.Either
import com.soyle.stories.character.repositories.CharacterRepository
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
        val buildResult = Character.buildNewCharacter(projectId.uuid, name)

        // couldn't just put into map {  } call below because addNewCharacter is a suspend function and map {  } isn't
        // inline, so the suspending scope is lost.
        if (buildResult is Either.Right) {
            characterRepository.addNewCharacter(buildResult.b)
        }

        buildResult.map {
            CharacterItem(
                it.id.uuid,
                it.name
            )
        }.fold(
            outputPort::receiveBuildNewCharacterFailure,
            outputPort::receiveBuildNewCharacterResponse
        )
    }
}