package com.soyle.stories.desktop.config.drivers.prose

import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver.Companion.driver
import com.soyle.stories.prose.proseEditor.MentionIssueMenu

fun MentionIssueMenu.removeMention() {
    val removeMentionOption = with(proseEditorView.driver()) {
        removeMentionOption()!!
    }
    proseEditorView.driver().interact {
        removeMentionOption.fire()
    }
}