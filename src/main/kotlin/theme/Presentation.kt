package com.soyle.stories.desktop.config.theme

import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgumentNotifier
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemovedNotifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.layout.config.dynamic.MoralArgument
import com.soyle.stories.theme.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArcNotifier
import com.soyle.stories.theme.moralArgument.*

object Presentation {

    init {
        scoped<MoralArgumentScope> {
            provide<MoralArgumentViewListener> {
                val presenter = MoralArgumentPresenter(
                    themeId,
                    get<MoralArgumentState>()
                )

                presenter listensTo projectScope.get<ArcSectionAddedToCharacterArcNotifier>()
                presenter listensTo projectScope.get<CharacterArcSectionMovedInMoralArgumentNotifier>()
                presenter listensTo projectScope.get<CharacterArcSectionRemovedNotifier>()

                MoralArgumentController(
                    themeId,
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get()
                )
            }
        }
    }

}