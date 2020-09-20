package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import java.util.*

class CreatedCharacterArcSection(
    val characterArcSectionId: UUID,
    val arcId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val templateSectionId: UUID,
    val templateSectionName: String,
    val value: String,
) {

    constructor(characterArcId: CharacterArc.Id, newSection: CharacterArcSection) : this(
        newSection.id.uuid,
        characterArcId.uuid,
        newSection.characterId.uuid,
        newSection.themeId.uuid,
        newSection.template.id.uuid,
        newSection.template.name,
        newSection.value
    )

}