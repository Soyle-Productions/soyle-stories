package com.soyle.stories.usecase.scene.common

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.getSceneDetails.CoveredArcSectionInScene
import java.util.*

data class IncludedCharacterInScene(
  val sceneId: Scene.Id,
  val characterId: Character.Id,
  val characterName: String,
  val roleInScene: RoleInScene?,
  val motivation: String?,
  val inheritedMotivation: InheritedMotivation?,
  val coveredArcSections: List<CoveredArcSectionInScene>
)