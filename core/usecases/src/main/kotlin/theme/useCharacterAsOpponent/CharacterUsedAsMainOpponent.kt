package com.soyle.stories.usecase.theme.useCharacterAsOpponent

import java.util.*

class CharacterUsedAsMainOpponent(
    val characterId: UUID,
    val characterName: String,
    val opponentOfCharacterId: UUID,
    val themeId: UUID
)