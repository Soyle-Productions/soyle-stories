package com.soyle.stories.desktop.config.theme

import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArcNotifier
import com.soyle.stories.theme.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArcReceiver
import com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation.ChangedThematicRevelationNotifier
import com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation.ChangedThematicRevelationReceiver
import com.soyle.stories.theme.changeThemeDetails.changeThemeLine.ChangedThemeLineNotifier
import com.soyle.stories.theme.changeThemeDetails.changeThemeLine.ChangedThemeLineReceiver
import com.soyle.stories.theme.deleteTheme.ThemeDeletedNotifier
import com.soyle.stories.theme.deleteTheme.ThemeDeletedReceiver

object Notifiers {

    init {
        scoped<ProjectScope> {
            provide(ArcSectionAddedToCharacterArcReceiver::class) {
                ArcSectionAddedToCharacterArcNotifier()
            }

            provide(ChangedThemeLineReceiver::class) {
                ChangedThemeLineNotifier()
            }

            provide(ChangedThematicRevelationReceiver::class) {
                ChangedThematicRevelationNotifier()
            }

            provide(ThemeDeletedReceiver::class) {
                ThemeDeletedNotifier()
            }
        }
    }

}