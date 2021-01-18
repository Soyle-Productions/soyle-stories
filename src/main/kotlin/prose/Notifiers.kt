package com.soyle.stories.desktop.config.prose

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedNotifier
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver

object Notifiers {

    init {
        scoped<ProjectScope> {
            mentionTextReplaced()
        }
    }

    private fun InProjectScope.mentionTextReplaced() {
        provide(MentionTextReplacedReceiver::class) {
            MentionTextReplacedNotifier()
        }
    }

}