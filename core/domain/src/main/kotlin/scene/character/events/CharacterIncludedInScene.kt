package com.soyle.stories.domain.scene.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene

data class CharacterIncludedInScene(
    override val sceneId: Scene.Id,
    override val characterId: Character.Id,
    val characterName: String,
    val projectId: Project.Id
) : CharacterInSceneEvent()