package com.soyle.stories.theme.usecases.removeCharacterAsOpponent

import java.util.*

class CharacterRemovedAsOpponent(
    val characterId: UUID,
    val opponentOfCharacterId: UUID,
    val themeId: UUID
)