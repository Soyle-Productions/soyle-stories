package com.soyle.stories.theme.includeCharacterInTheme

import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme

interface CharacterIncludedInThemeReceiver {
    suspend fun receiveCharacterIncludedInTheme(characterIncludedInTheme: CharacterIncludedInTheme)
}