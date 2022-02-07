package com.soyle.stories.usecase.scene.character.involve

import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedInStoryEvent

interface InvolveCharacterInScene {

    suspend operator fun invoke(event: CharacterInvolvedInStoryEvent, output: OutputPort)

    interface OutputPort {
        suspend fun characterInvolvedInScene(event: CharacterInvolvedInScene)
        suspend fun sourceAddedToCharacterInScene(event: SourceAddedToCharacterInScene)
    }

}