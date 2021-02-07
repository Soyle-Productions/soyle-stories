package com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import java.util.*

class ArcSectionAddedToCharacterArc(
    val characterArcSectionId: UUID,
    val arcId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val templateSectionId: UUID,
    val templateSectionName: String,
    val indexInMoralArgument: Int?,
    val value: String,
    val displacedArcSections: List<UUID>
) {

    constructor(characterArc: CharacterArc, newSection: CharacterArcSection) : this(
        newSection.id.uuid,
        characterArc.id.uuid,
        newSection.characterId.uuid,
        newSection.themeId.uuid,
        newSection.template.id.uuid,
        newSection.template.name,
        characterArc.indexInMoralArgument(newSection.id),
        newSection.value,
        characterArc.indexInMoralArgument(newSection.id)?.let { indexInMoralArgument ->
            characterArc.moralArgument().arcSections.withIndex().filter { it.index > indexInMoralArgument }.map {
                it.value.id.uuid
            }
        } ?: listOf()
    )

}