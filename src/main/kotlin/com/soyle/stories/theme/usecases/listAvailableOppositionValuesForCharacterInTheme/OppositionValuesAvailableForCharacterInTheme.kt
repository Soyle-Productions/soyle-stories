package com.soyle.stories.theme.usecases.listAvailableOppositionValuesForCharacterInTheme

import com.soyle.stories.theme.usecases.listOppositionsInValueWeb.OppositionValueItem
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ValueWebItem
import java.util.*

class OppositionValuesAvailableForCharacterInTheme(
    val themeId: UUID,
    val characterId: UUID,
    private val oppositionValues: List<AvailableValueWebForCharacterInTheme>
) : List<AvailableValueWebForCharacterInTheme> by oppositionValues

class AvailableValueWebForCharacterInTheme(
    valueWebId: UUID,
    valueWebName: String,
    private val oppositionValues: List<AvailableOppositionValueForCharacterInTheme>
) : ValueWebItem(valueWebId, valueWebName), List<AvailableOppositionValueForCharacterInTheme> by oppositionValues {

    val characterRepresentsAnOpposition: Boolean by lazy {
        oppositionValues.any { it.characterRepresentsValue }
    }
}

class AvailableOppositionValueForCharacterInTheme(
    val oppositionValueId: UUID,
    val oppositionValueName: String,
    val characterRepresentsValue: Boolean
)