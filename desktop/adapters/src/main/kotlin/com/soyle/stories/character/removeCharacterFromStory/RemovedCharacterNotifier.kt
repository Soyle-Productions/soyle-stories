package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.usecase.character.remove.RemovedCharacter
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory

class RemovedCharacterNotifier : Receiver<CharacterRemovedFromStory>, Notifier<Receiver<CharacterRemovedFromStory>>() {

    override suspend fun receiveEvent(event: CharacterRemovedFromStory) {
        notifyAll { it.receiveEvent(event) }
    }

}