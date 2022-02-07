package com.soyle.stories.usecase.scene.character.list

import com.soyle.stories.domain.storyevent.StoryEvent

data class CharacterInSceneSourceItem(
    val storyEvent: StoryEvent.Id,
    val name: String
)