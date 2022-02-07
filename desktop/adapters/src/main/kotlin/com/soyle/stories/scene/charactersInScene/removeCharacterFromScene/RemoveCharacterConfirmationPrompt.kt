package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.usecase.scene.storyevent.list.StoryEventInSceneItem

fun interface RemoveCharacterConfirmationPrompt {
    suspend fun confirmRemoval(
        sceneName: String,
        characterName: String,
        stillInvolvedIn: List<StoryEventInSceneItem>
    )
}