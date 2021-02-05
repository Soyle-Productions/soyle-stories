package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemovedCharacter

interface RemovedCharacterReceiver {

    suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter)

}