package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import java.util.*

class AvailableCharacterArcSectionsForCharacterInScene(
    val sceneId: UUID,
    val characterId: UUID,
    arcs: List<CharacterArcUsedInScene>
) : List<CharacterArcUsedInScene> by arcs

class CharacterArcUsedInScene(
    val characterId: UUID,
    val characterArcId: UUID,
    val themeId: UUID,
    val characterArcName: String,
    sections: List<ArcSectionUsedInScene>
) : List<ArcSectionUsedInScene> by sections

class ArcSectionUsedInScene(
    val arcSectionId: UUID,
    val templateName: String,
    val sectionValue: String,
    val usedInScene: Boolean,
    val isMultiTemplate: Boolean
)