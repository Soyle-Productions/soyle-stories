package com.soyle.stories.desktop.config.prose

import com.soyle.stories.common.listensTo
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedNotifier
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeNotifier
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeReceiver
import com.soyle.stories.theme.removeSymbolicItem.RemoveSymbolicItemControllerImpl

object Notifiers {

    init {
        scoped<ProjectScope> {
            mentionTextReplaced()
            symbolRemovedFromTheme()
        }
    }

    private fun InProjectScope.mentionTextReplaced() {
        provide(MentionTextReplacedReceiver::class) {
            MentionTextReplacedNotifier()
        }
    }

    private fun InProjectScope.symbolRemovedFromTheme() {
        provide(SymbolRemovedFromThemeReceiver::class) {
            SymbolRemovedFromThemeNotifier().also {
                get<RemoveSymbolicItemControllerImpl>() listensTo it
            }
        }
    }

}