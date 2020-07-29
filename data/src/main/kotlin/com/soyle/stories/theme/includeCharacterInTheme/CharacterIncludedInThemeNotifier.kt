package com.soyle.stories.theme.includeCharacterInTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import kotlin.coroutines.coroutineContext

class CharacterIncludedInThemeNotifier : CharacterIncludedInThemeReceiver, Notifier<CharacterIncludedInThemeReceiver>() {
    override suspend fun receiveCharacterIncludedInTheme(characterIncludedInTheme: CharacterIncludedInTheme) {
        notifyAll(coroutineContext) { it.receiveCharacterIncludedInTheme(characterIncludedInTheme) }
    }
}