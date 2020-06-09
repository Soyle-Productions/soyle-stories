package com.soyle.stories.di.characterarc

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterNotifier
import com.soyle.stories.character.characterList.LiveCharacterList
import com.soyle.stories.characterarc.characterList.CharacterListController
import com.soyle.stories.characterarc.characterList.CharacterListModel
import com.soyle.stories.characterarc.characterList.CharacterListPresenter
import com.soyle.stories.characterarc.characterList.CharacterListViewListener
import com.soyle.stories.characterarc.eventbus.RemoveCharacterFromLocalStoryNotifier
import com.soyle.stories.characterarc.eventbus.RenameCharacterNotifier
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope

internal object CharacterListModule {

	init {

		scoped<ProjectScope> {

			provide {
				LiveCharacterList(
				  applicationScope.get(),
				  get(),
				  get<BuildNewCharacterNotifier>(),
				  get<RemoveCharacterFromLocalStoryNotifier>(),
				  get<RenameCharacterNotifier>()
				)
			}

			provide<CharacterListViewListener> {
				val characterListPresenter = CharacterListPresenter(
				  applicationScope.get(),
				  get<CharacterListModel>(),
				  get(),
				  get()
				)

				CharacterListController(
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
				  get(),
				  get()
				)
			}

		}

	}
}