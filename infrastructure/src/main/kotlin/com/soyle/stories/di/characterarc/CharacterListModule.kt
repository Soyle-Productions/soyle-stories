package com.soyle.stories.di.characterarc

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterNotifier
import com.soyle.stories.character.characterList.LiveCharacterList
import com.soyle.stories.character.deleteCharacterArc.DeleteCharacterArcNotifier
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.character.renameCharacter.RenamedCharacterNotifier
import com.soyle.stories.characterarc.characterList.CharacterListController
import com.soyle.stories.characterarc.characterList.CharacterListModel
import com.soyle.stories.characterarc.characterList.CharacterListPresenter
import com.soyle.stories.characterarc.characterList.CharacterListViewListener
import com.soyle.stories.characterarc.eventbus.RenameCharacterArcNotifier
import com.soyle.stories.characterarc.planNewCharacterArc.CreatedCharacterArcNotifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeNotifier
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver

internal object CharacterListModule {

    init {

        scoped<ProjectScope> {

            provide {
                LiveCharacterList(
                    get<CreatedCharacterNotifier>(),
                    get<RemovedCharacterNotifier>(),
                    get<RenamedCharacterNotifier>()
                )
            }

            provide<CharacterListViewListener> {
                val characterListPresenter = CharacterListPresenter(
                    get<CharacterListModel>(),
                    get()
                )

                characterListPresenter listensTo get<CreatedCharacterArcNotifier>()
                characterListPresenter listensTo get<CharacterIncludedInThemeNotifier>()
                characterListPresenter listensTo get<DeleteCharacterArcNotifier>()
                characterListPresenter listensTo get<RenameCharacterArcNotifier>()

                CharacterListController(
                    projectId.toString(),
                    applicationScope.get(),
                    get(),
                    characterListPresenter,
                    get(),
                    get(),
                    get(),
                    get(),
                    get(),
                    get(),
                    get(),
                    get()
                )
            }

        }

    }
}