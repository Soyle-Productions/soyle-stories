package com.soyle.stories.theme.removeValueWebFromTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.RemoveValueWebFromTheme
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.ValueWebRemovedFromTheme
import kotlin.coroutines.coroutineContext

class RemoveValueWebFromThemeNotifier : Notifier<RemoveValueWebFromTheme.OutputPort>(), RemoveValueWebFromTheme.OutputPort {

    override suspend fun removedValueWebFromTheme(response: ValueWebRemovedFromTheme) {
        notifyAll { it.removedValueWebFromTheme(response) }
    }

}