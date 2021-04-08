package com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene

import java.util.*

class CharacterArcSectionTypes(
    val requiredTypes: List<CharacterArcSectionType>,
    val additionalTypes: List<CharacterArcSectionType>
)

class CharacterArcSectionType(
    val templateSectionId: UUID,
    val name: String
)