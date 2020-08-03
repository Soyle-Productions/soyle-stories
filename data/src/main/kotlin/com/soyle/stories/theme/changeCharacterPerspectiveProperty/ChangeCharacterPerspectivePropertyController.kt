package com.soyle.stories.theme.changeCharacterPerspectiveProperty

interface ChangeCharacterPerspectivePropertyController {

    fun setAttackByOpponent(
        themeId: String,
        perspectiveCharacterId: String,
        opponentId: String,
        attack: String
    )

    fun setSimilaritiesBetweenCharacters(
        themeId: String,
        perspectiveCharacterId: String,
        opponentId: String,
        similarities: String
    )

}