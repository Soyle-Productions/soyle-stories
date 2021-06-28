package com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters

import java.util.*

class AvailablePerspectiveCharacters(
    val themeId: UUID,
    private val characters: List<AvailablePerspectiveCharacter>
) : List<AvailablePerspectiveCharacter> by characters

class AvailablePerspectiveCharacter(
    val characterId: UUID,
    val characterName: String,
    val isMajorCharacter: Boolean
)