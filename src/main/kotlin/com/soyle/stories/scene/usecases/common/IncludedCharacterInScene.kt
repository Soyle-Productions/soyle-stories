package com.soyle.stories.scene.usecases.common

import java.util.*

class IncludedCharacterInScene(
  val sceneId: UUID,
  val characterId: UUID,
  val characterName: String,
  val motivation: String?,
  val inheritedMotivation: InheritedMotivation?
)