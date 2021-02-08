package com.soyle.stories.theme.removeValueWebFromTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.removeValueWebFromTheme.RemoveValueWebFromTheme
import com.soyle.stories.usecase.theme.removeValueWebFromTheme.ValueWebRemovedFromTheme

class RemoveValueWebFromThemeNotifier : Notifier<RemoveValueWebFromTheme.OutputPort>(), RemoveValueWebFromTheme.OutputPort {

    override suspend fun removedValueWebFromTheme(response: ValueWebRemovedFromTheme) {
        notifyAll { it.removedValueWebFromTheme(response) }
    }

}