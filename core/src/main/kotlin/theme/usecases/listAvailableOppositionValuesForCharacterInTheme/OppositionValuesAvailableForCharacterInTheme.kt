package com.soyle.stories.theme.usecases.listAvailableOppositionValuesForCharacterInTheme

import com.soyle.stories.theme.usecases.listOppositionsInValueWeb.OppositionValueItem
import com.soyle.stories.theme.usecases.listOppositionsInValueWeb.OppositionValueWithSymbols
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