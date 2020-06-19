package com.soyle.stories.theme.deleteTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme

class DeleteThemeNotifier : Notifier<DeleteTheme.OutputPort>(), DeleteTheme.OutputPort {

    override fun themeDeleted(response: DeletedTheme) {
        notifyAll { it.themeDeleted(response) }
    }

}