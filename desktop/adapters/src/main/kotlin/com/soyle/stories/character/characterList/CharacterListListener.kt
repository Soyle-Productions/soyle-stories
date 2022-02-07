package com.soyle.stories.character.characterList

import com.soyle.stories.character.buildNewCharacter.CharacterCreatedReceiver
import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory

interface CharacterListListener : CharacterCreatedReceiver, CharacterRenamedReceiver, Receiver<CharacterRemovedFromStory>