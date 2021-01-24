package com.soyle.stories.theme.removeCharacterFromComparison

import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemovedCharacterFromTheme

interface RemovedCharacterFromThemeReceiver {

    suspend fun receiveRemovedCharacterFromTheme(removedCharacterFromTheme: RemovedCharacterFromTheme)

}