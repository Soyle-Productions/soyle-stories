package com.soyle.stories.theme.includeCharacterInTheme

import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme

interface CharacterIncludedInThemeReceiver {
    suspend fun receiveCharacterIncludedInTheme(characterIncludedInTheme: CharacterIncludedInTheme)
}