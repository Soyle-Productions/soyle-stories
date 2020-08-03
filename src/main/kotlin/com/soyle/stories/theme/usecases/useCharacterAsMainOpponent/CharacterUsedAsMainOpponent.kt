package com.soyle.stories.theme.usecases.useCharacterAsMainOpponent

import java.util.*

class CharacterUsedAsMainOpponent(
    val characterId: UUID,
    val characterName: String,
    val opponentOfCharacterId: UUID,
    val themeId: UUID
)