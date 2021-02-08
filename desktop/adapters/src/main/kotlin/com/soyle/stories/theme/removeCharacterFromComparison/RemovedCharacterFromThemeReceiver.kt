package com.soyle.stories.theme.removeCharacterFromComparison

import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemovedCharacterFromTheme

interface RemovedCharacterFromThemeReceiver {

    suspend fun receiveRemovedCharacterFromTheme(removedCharacterFromTheme: RemovedCharacterFromTheme)

}