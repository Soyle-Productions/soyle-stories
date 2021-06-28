package com.soyle.stories.usecase.character.arc.planNewCharacterArc

import com.soyle.stories.domain.character.CharacterArc
import java.util.*

class CreatedCharacterArc(
    val themeId: UUID,
    val characterId: UUID,
    val characterArcName: String
) {

    constructor(arc: CharacterArc) : this(arc.themeId.uuid, arc.characterId.uuid, arc.name)

}