package com.soyle.stories.di.characterarc

import com.soyle.stories.character.buildNewCharacter.CharacterCreatedNotifier
import com.soyle.stories.character.characterList.LiveCharacterList
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.character.renameCharacter.CharacterRenamedNotifier
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope

internal object CharacterListModule {

    init {

        scoped<ProjectScope> {

            provide {
                LiveCharacterList(
                    get<CharacterCreatedNotifier>(),
                    get<RemovedCharacterNotifier>(),
                    get<CharacterRenamedNotifier>()
                )
            }

        }

    }
}