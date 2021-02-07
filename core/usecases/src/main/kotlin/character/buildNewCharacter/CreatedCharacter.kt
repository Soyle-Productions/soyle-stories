package com.soyle.stories.usecase.character.buildNewCharacter

import java.util.*

class CreatedCharacter(
    val characterId: UUID,
    val characterName: String,
    val mediaId: UUID?
)