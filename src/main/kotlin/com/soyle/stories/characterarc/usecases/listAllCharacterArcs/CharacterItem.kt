package com.soyle.stories.characterarc.usecases.listAllCharacterArcs

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.theme.characterInTheme.CharacterInTheme
import java.util.*

/**
 * Created by Brendan
 * Date: 2/23/2020
 * Time: 11:58 AM
 */
class CharacterItem(
    val characterId: UUID,
    val characterName: String,
    val mediaId: UUID?
) {

    constructor(character: Character) : this(character.id.uuid, character.name, character.media?.uuid)
    constructor(character: CharacterInTheme) : this(character.id.uuid, character.name, null)
}