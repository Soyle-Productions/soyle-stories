package com.soyle.stories.theme.includeCharacterInTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme

class CharacterIncludedInThemeNotifier : CharacterIncludedInThemeReceiver, Notifier<CharacterIncludedInThemeReceiver>() {
    override suspend fun receiveCharacterIncludedInTheme(characterIncludedInTheme: CharacterIncludedInTheme) {
        notifyAll { it.receiveCharacterIncludedInTheme(characterIncludedInTheme) }
    }
}