package com.soyle.stories.theme.removeCharacterFromComparison

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison
import java.util.*

class RemoveCharacterFromComparisonControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeCharacterFromComparison: RemoveCharacterFromComparison,
    private val removeCharacterFromComparisonOutputPort: RemoveCharacterFromComparison.OutputPort
) : RemoveCharacterFromComparisonController {

    override fun removeCharacter(themeId: String, characterId: String) {
        val preparedThemeId = UUID.fromString(themeId)
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            removeCharacterFromComparison.invoke(
                preparedThemeId,
                preparedCharacterId,
                removeCharacterFromComparisonOutputPort
            )
        }
    }

}