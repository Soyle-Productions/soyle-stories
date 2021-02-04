package com.soyle.stories.theme.includeCharacterInTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import java.util.*

class IncludeCharacterInComparisonControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val includeCharacterInComparison: IncludeCharacterInComparison,
    private val includeCharacterInComparisonOutputPort: IncludeCharacterInComparison.OutputPort
) : IncludeCharacterInComparisonController {

    override fun includeCharacterInTheme(themeId: String, characterId: String) {
        val preparedCharacterId = UUID.fromString(characterId)
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            includeCharacterInComparison.invoke(
                preparedCharacterId,
                preparedThemeId,
                includeCharacterInComparisonOutputPort
            )
        }
    }

}