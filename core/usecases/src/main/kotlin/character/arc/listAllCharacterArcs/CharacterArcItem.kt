package com.soyle.stories.usecase.character.arc.listAllCharacterArcs

import com.soyle.stories.domain.character.CharacterArc
import java.util.*

class CharacterArcItem(
    val characterId: UUID,
    val characterArcId: UUID,
    val characterArcName: String,
    val themeId: UUID
) {

    constructor(characterArc: CharacterArc) : this(characterArc.characterId.uuid, characterArc.id.uuid, characterArc.name, characterArc.themeId.uuid)

}