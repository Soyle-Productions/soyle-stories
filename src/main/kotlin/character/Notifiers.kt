package com.soyle.stories.desktop.config.character

import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgumentNotifier
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgumentReceiver
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemovedNotifier
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemovedReceiver
import com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgument
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope

object Notifiers {

    init {
        scoped<ProjectScope> {

            provide(CharacterArcSectionMovedInMoralArgumentReceiver::class) {
                CharacterArcSectionMovedInMoralArgumentNotifier()
            }

            provide(CharacterArcSectionRemovedReceiver::class) {
                CharacterArcSectionRemovedNotifier()
            }

        }
    }

}