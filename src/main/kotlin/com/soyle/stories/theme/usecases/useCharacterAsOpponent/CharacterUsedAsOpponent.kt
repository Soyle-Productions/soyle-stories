package com.soyle.stories.theme.usecases.useCharacterAsOpponent

import java.util.*

class CharacterUsedAsOpponent(
    val characterId: UUID,
    val characterName: String,
    val opponentOfCharacterId: UUID,
    val themeId: UUID
)