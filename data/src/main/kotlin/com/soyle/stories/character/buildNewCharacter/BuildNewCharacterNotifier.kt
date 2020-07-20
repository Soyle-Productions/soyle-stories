package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonNotifier
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme

class BuildNewCharacterNotifier(
    private val characterIncludedInComparisonNotifier: IncludeCharacterInComparisonNotifier
) : BuildNewCharacter.OutputPort, Notifier<BuildNewCharacter.OutputPort>() {

    override fun receiveBuildNewCharacterFailure(failure: CharacterException) {
        notifyAll { it.receiveBuildNewCharacterFailure(failure) }
    }

    override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
        notifyAll { it.receiveBuildNewCharacterResponse(response) }
    }

    override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) {
        characterIncludedInComparisonNotifier.receiveIncludeCharacterInComparisonResponse(response)
    }
}