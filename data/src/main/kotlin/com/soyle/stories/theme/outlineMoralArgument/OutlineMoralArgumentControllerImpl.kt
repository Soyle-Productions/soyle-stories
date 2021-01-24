package com.soyle.stories.theme.outlineMoralArgument

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.outlineMoralArgument.OutlineMoralArgumentForCharacterInTheme
import java.util.*

class OutlineMoralArgumentControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val outlineMoralArgument: OutlineMoralArgumentForCharacterInTheme,
    private val outlineMoralArgumentOutputPort: OutlineMoralArgumentForCharacterInTheme.OutputPort
) : OutlineMoralArgumentController {

    override fun outlineMoralArgument(themeId: String, characterId: String) {
        val preparedThemeId = UUID.randomUUID()
        val preparedCharacterId = UUID.randomUUID()
        threadTransformer.async {
            outlineMoralArgument.invoke(
                preparedThemeId,
                preparedCharacterId,
                outlineMoralArgumentOutputPort
            )
        }
    }

}