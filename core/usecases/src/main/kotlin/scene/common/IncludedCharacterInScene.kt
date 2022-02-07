package com.soyle.stories.usecase.scene.common

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.getSceneDetails.CoveredArcSectionInScene

data class IncludedCharacterInScene(
    val sceneId: Scene.Id,
    val characterId: Character.Id,
    val characterName: String,
    val roleInScene: RoleInScene?,
    val desire: String,
    val motivation: String?,
    val inheritedMotivation: InheritedMotivation?,
    val coveredArcSections: List<CoveredArcSectionInScene>
)