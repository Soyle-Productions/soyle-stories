package com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme

import com.soyle.stories.usecase.theme.listOppositionsInValueWeb.OppositionValueItem
import com.soyle.stories.usecase.theme.listValueWebsInTheme.ValueWebItem
import java.util.*

class OppositionValuesAvailableForCharacterInTheme(
    val themeId: UUID,
    val characterId: UUID,
    private val oppositionValues: List<AvailableValueWebForCharacterInTheme>
) : List<AvailableValueWebForCharacterInTheme> by oppositionValues

class AvailableValueWebForCharacterInTheme(
    valueWebId: UUID,
    valueWebName: String,
    val oppositionCharacterRepresents: OppositionValueItem?,
    private val oppositionValues: List<AvailableOppositionValueForCharacterInTheme>
) : ValueWebItem(valueWebId, valueWebName), List<AvailableOppositionValueForCharacterInTheme> by oppositionValues {

    val characterRepresentsAnOpposition: Boolean
        get() = oppositionCharacterRepresents != null
}



class AvailableOppositionValueForCharacterInTheme(
    oppositionValueId: UUID,
    oppositionValueName: String
) : OppositionValueItem(oppositionValueId, oppositionValueName)