package com.soyle.stories.usecase.scene.coverCharacterArcSectionsInScene

import java.util.*

class AvailableCharacterArcSectionTypesForCharacterArc(
    val characterId: UUID,
    val themeId: UUID,
    sections: List<AvailableCharacterArcSectionType>
) : List<AvailableCharacterArcSectionType> by sections

class AvailableCharacterArcSectionType(
    val templateSectionId: UUID,
    val name: String,
    val multiple: Boolean,
    val existingSection: Pair<UUID, String>?
)