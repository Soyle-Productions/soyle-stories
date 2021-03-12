package com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene

import java.util.*

class CharacterArcSectionCoveredByScene(
    val sceneId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val characterArcId: UUID,
    val characterArcSectionId: UUID,
    val characterArcSectionName: String,
    val characterArcSectionValue: String,
    val characterArcName: String,
    val isMultiTemplate: Boolean
)