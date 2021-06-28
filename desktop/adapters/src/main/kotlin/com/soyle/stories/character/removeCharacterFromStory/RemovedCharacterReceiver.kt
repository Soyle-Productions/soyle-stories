package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.usecase.character.removeCharacterFromStory.RemovedCharacter

interface RemovedCharacterReceiver {

    suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter)

}