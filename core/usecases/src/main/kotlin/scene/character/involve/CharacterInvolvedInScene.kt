package com.soyle.stories.usecase.scene.character.involve

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneSourceItem
import com.soyle.stories.usecase.scene.common.InheritedMotivation

data class CharacterInvolvedInScene(
    val project: Project.Id,
    val scene: Scene.Id,
    val character: Character.Id,
    val characterName: String,
    val inheritedMotivation: InheritedMotivation?,
    val source: CharacterInSceneSourceItem
)