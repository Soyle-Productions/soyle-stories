package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.changeSectionValue.ChangedCharacterArcSectionValueNotifier
import com.soyle.stories.characterarc.eventbus.ChangeCharacterPropertyValueNotifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.theme.changeCharacterChange.ChangedCharacterChangeNotifier
import com.soyle.stories.theme.changeCharacterPerspectiveProperty.CharacterPerspectivePropertyChangedNotifier
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeNotifier
import com.soyle.stories.theme.removeCharacterAsOpponent.CharacterRemovedAsOpponentNotifier
import com.soyle.stories.theme.removeCharacterFromComparison.RemovedCharacterFromThemeNotifier
import com.soyle.stories.theme.changeThemeDetails.ThemeWithCentralConflictChangedNotifier
import com.soyle.stories.theme.useCharacterAsMainOpponent.CharacterUsedAsMainOpponentNotifier
import com.soyle.stories.theme.useCharacterAsOpponent.CharacterUsedAsOpponentNotifier

object CharacterConflictModule {

    init {
        scoped<CharacterConflictScope> {

            provide<CharacterConflictViewListener> {

                val presenter = CharacterConflictPresenter(
                    themeId,
                    get<CharacterConflictModel>()
                )

                presenter listensTo projectScope.get<CharacterUsedAsOpponentNotifier>()
                presenter listensTo projectScope.get<CharacterUsedAsMainOpponentNotifier>()
                presenter listensTo projectScope.get<ThemeWithCentralConflictChangedNotifier>()
                presenter listensTo projectScope.get<ChangedCharacterArcSectionValueNotifier>()
                presenter listensTo projectScope.get<ChangedCharacterChangeNotifier>()
                presenter listensTo projectScope.get<ChangeCharacterPropertyValueNotifier>()
                presenter listensTo projectScope.get<CharacterPerspectivePropertyChangedNotifier>()
                presenter listensTo projectScope.get<CharacterRemovedAsOpponentNotifier>()
                presenter listensTo projectScope.get<RemovedCharacterFromThemeNotifier>()

                CharacterConflictController(
                    themeId,
                    projectScope.applicationScope.get(),
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
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get()
                ).also {
                    it listensTo projectScope.get<CharacterIncludedInThemeNotifier>()
                }
            }
        }
    }

}