package com.soyle.stories.usecase.theme.removeCharacterAsOpponent

import java.util.*

class CharacterRemovedAsOpponent(
    val characterId: UUID,
    val opponentOfCharacterId: UUID,
    val themeId: UUID
)