package com.soyle.stories.theme.createTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import kotlin.coroutines.coroutineContext

class CreateThemeNotifier : Notifier<CreateTheme.OutputPort>(), CreateTheme.OutputPort {
    override suspend fun themeCreated(response: CreatedTheme) {
        notifyAll(coroutineContext) {
            it.themeCreated(response)
        }
    }
}