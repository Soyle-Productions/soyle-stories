package com.soyle.stories.theme.includeCharacterInTheme

import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.includeCharacterInComparison.IncludeCharacterInComparison

class IncludeCharacterInComparisonOutput(
    private val characterIncludedInThemeReceiver: CharacterIncludedInThemeReceiver
) : IncludeCharacterInComparison.OutputPort {
    override fun receiveIncludeCharacterInComparisonFailure(failure: Exception) {
        throw failure
    }

    override suspend fun receiveIncludeCharacterInComparisonResponse(response: CharacterIncludedInTheme) {
        characterIncludedInThemeReceiver.receiveCharacterIncludedInTheme(response)
    }
}