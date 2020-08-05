package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.theme.useCharacterAsOpponent.CharacterUsedAsOpponentReceiver
import com.soyle.stories.theme.useCharacterAsOpponent.UseCharacterAsOpponentOutput
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.CharacterUsedAsOpponent
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponent

class BuildNewCharacterOutput(
    private val createdCharacterReceiver: CreatedCharacterReceiver,
    private val characterIncludedInThemeReceiver: CharacterIncludedInThemeReceiver,
    private val useCharacterAsOpponentReceiver: CharacterUsedAsOpponentReceiver
) : BuildNewCharacter.OutputPort {

    override fun receiveBuildNewCharacterFailure(failure: CharacterException) {
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