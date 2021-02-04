package com.soyle.stories.theme.usecases.examineCentralConflictOfTheme

import java.util.*

class ExaminedCentralConflict(
    val themeId: UUID,
    val centralConflict: String,
    val characterChange: ExaminedCharacterChange?
)

class ExaminedCharacterChange(
    val characterId: UUID,
    val characterName: String,
    val desire: String,
    val psychologicalWeakness: String,
    val moralWeakness: String,
    val changeAtEnd: String,
    val opponents: CharacterChangeOpponents
)

class CharacterChangeOpponents(private val opponents: List<CharacterChangeOpponent>) : List<CharacterChangeOpponent> by opponents

class CharacterChangeOpponent(
    val characterId: UUID,
    val characterName: String,
    val attack: String,
    val similarities: String,
    val powerStatusOrAbility: String,
    val isMainOpponent: Boolean
)