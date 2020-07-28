package com.soyle.stories.theme.usecases.useCharacterAsOpponent

import java.util.*

class OpponentCharacter(
    val characterId: UUID,
    val characterName: String,
    val opponentOfCharacterId: UUID,
    val themeId: UUID
)