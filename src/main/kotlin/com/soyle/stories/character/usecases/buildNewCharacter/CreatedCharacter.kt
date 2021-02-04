package com.soyle.stories.character.usecases.buildNewCharacter

import java.util.*

class CreatedCharacter(
    val characterId: UUID,
    val characterName: String,
    val mediaId: UUID?
)