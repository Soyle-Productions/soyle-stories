package com.soyle.stories.theme.renameTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.renameTheme.RenameTheme
import com.soyle.stories.theme.usecases.renameTheme.RenamedTheme

class RenameThemeNotifier : Notifier<RenameTheme.OutputPort>(), RenameTheme.OutputPort {
    override fun themeRenamed(response: RenamedTheme) {
        notifyAll { it.themeRenamed(response) }
    }
}