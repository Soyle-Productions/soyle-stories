package com.soyle.stories.character.characterList

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterReceiver
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver

interface CharacterListListener : CreatedCharacterReceiver, CharacterRenamedReceiver, RemovedCharacterReceiver