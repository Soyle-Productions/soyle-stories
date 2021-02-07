package com.soyle.stories.usecase.scene.common

import com.soyle.stories.usecase.scene.getSceneDetails.CoveredArcSectionInScene
import java.util.*

class IncludedCharacterInScene(
  val sceneId: UUID,
  val characterId: UUID,
  val characterName: String,
  val motivation: String?,
  val inheritedMotivation: InheritedMotivation?,
  val coveredArcSections: List<CoveredArcSectionInScene>
)