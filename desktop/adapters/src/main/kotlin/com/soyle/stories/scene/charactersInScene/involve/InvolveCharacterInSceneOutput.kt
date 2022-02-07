package com.soyle.stories.scene.charactersInScene.involve

import com.soyle.stories.scene.charactersInScene.source.added.SourceAddedToCharacterInSceneReceiver
import com.soyle.stories.usecase.scene.character.involve.CharacterInvolvedInScene
import com.soyle.stories.usecase.scene.character.involve.InvolveCharacterInScene
import com.soyle.stories.usecase.scene.character.involve.SourceAddedToCharacterInScene

class InvolveCharacterInSceneOutput(
    private val characterInvolvedInSceneReceiver: CharacterInvolvedInSceneReceiver,
    private val sourceAddedToCharacterInSceneReceiver: SourceAddedToCharacterInSceneReceiver
) : InvolveCharacterInScene.OutputPort {
    override suspend fun characterInvolvedInScene(event: CharacterInvolvedInScene) {
        println("output $event")
        characterInvolvedInSceneReceiver.receiveCharacterInvolvedInScene(event)
    }

    override suspend fun sourceAddedToCharacterInScene(event: SourceAddedToCharacterInScene) {
        println("output $event")
        sourceAddedToCharacterInSceneReceiver.receiverSourceAddedToCharacterInScene(event)
    }
}