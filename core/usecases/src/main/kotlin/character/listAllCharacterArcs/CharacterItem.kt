package com.soyle.stories.usecase.character.listAllCharacterArcs

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import java.util.*

class CharacterItem(
    val characterId: UUID,
    val characterName: String,
    val mediaId: UUID?
) {

    constructor(character: Character) : this(character.id.uuid, character.name.value, character.media?.uuid)
    constructor(character: CharacterInTheme) : this(character.id.uuid, character.name, null)
}