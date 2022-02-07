package com.soyle.stories.character.characterList

import com.soyle.stories.character.buildNewCharacter.CharacterCreatedReceiver
import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory

class LiveCharacterList(
    private val createdCharacterNotifier: Notifier<CharacterCreatedReceiver>,
    private val removedCharacterNotifier: Notifier<Receiver<CharacterRemovedFromStory>>,
    private val characterRenamedNotifier: Notifier<CharacterRenamedReceiver>
) {

    fun addListener(listener: CharacterListListener) {
        createdCharacterNotifier.addListener(listener)
        characterRenamedNotifier.addListener(listener)
        removedCharacterNotifier.addListener(listener)
    }

}