package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.theme.useCharacterAsOpponent.CharacterUsedAsOpponentReceiver
import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter
import com.soyle.stories.usecase.character.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent

class BuildNewCharacterOutput(
    private val createdCharacterReceiver: CreatedCharacterReceiver,
    private val characterIncludedInThemeReceiver: CharacterIncludedInThemeReceiver,
    private val useCharacterAsOpponentReceiver: CharacterUsedAsOpponentReceiver
) : BuildNewCharacter.OutputPort {

    override fun receiveBuildNewCharacterFailure(failure: Exception) {
        throw failure
    }

    override suspend fun receiveBuildNewCharacterResponse(response: CharacterItem) {
        createdCharacterReceiver.receiveCreatedCharacter(CreatedCharacter(
            response.characterId, response.characterName, response.mediaId
        ))
    }

    override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) {
        characterIncludedInThemeReceiver.receiveCharacterIncludedInTheme(response)
    }

    override suspend fun characterIsOpponent(response: CharacterUsedAsOpponent) {
        useCharacterAsOpponentReceiver.receiveCharacterUsedAsOpponent(response)
    }
}