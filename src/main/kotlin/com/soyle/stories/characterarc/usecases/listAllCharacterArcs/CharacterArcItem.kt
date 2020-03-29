package com.soyle.stories.characterarc.usecases.listAllCharacterArcs

import java.util.*

/**
 * Created by Brendan
 * Date: 2/23/2020
 * Time: 11:58 AM
 */
class CharacterArcItem(
    val characterId: UUID,
    val characterArcName: String,
    val themeId: UUID
)