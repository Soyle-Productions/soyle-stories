package com.soyle.stories.desktop.config.project

import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterConfirmationNotifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.dialogs.ActiveDialogsPresenter
import com.soyle.stories.project.dialogs.ActiveDialogsState

object Presentation {

    init {
        scoped<ProjectScope> {
            activeDialogs()
        }
    }

    private fun InProjectScope.activeDialogs() {
        provide {
            ActiveDialogsPresenter(get<ActiveDialogsState>()).also {
                it listensTo get<RemoveCharacterConfirmationNotifier>()
            }
        }
    }

}