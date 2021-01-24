package com.soyle.stories.character.characterList

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterReceiver
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.common.Notifier

class LiveCharacterList(
    private val createdCharacterNotifier: Notifier<CreatedCharacterReceiver>,
    private val removedCharacterNotifier: Notifier<RemovedCharacterReceiver>,
    private val characterRenamedNotifier: Notifier<CharacterRenamedReceiver>
) {

    fun addListener(listener: CharacterListListener) {
        createdCharacterNotifier.addListener(listener)
        characterRenamedNotifier.addListener(listener)
        removedCharacterNotifier.addListener(listener)
    }

}