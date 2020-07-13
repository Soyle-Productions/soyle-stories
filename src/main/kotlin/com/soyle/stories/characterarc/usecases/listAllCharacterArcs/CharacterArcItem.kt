package com.soyle.stories.characterarc.usecases.listAllCharacterArcs

import com.soyle.stories.entities.CharacterArc
import java.util.*

/**
 * Created by Brendan
 * Date: 2/23/2020
 * Time: 11:58 AM
 */
class CharacterArcItem(
    val characterId: UUID,
    val characterArcName: String,
    val themeId: UUID
) {

    constructor(characterArc: CharacterArc) : this(characterArc.characterId.uuid, characterArc.name, characterArc.themeId.uuid)

}