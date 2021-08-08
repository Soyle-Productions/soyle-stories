package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.promoteMinorCharacter.PromoteMinorCharacter
import kotlinx.coroutines.Job
import java.util.*

class PromoteMinorCharacterController(
    private val threadTransformer: ThreadTransformer,
    private val promoteMinorCharacter: PromoteMinorCharacter,
    private val promoteMinorCharacterOutputPort: PromoteMinorCharacter.OutputPort
) {

    fun promoteCharacter(themeId: String, characterId: String): Job {
        val preparedThemeId = UUID.fromString(themeId)
        val preparedCharacterId = UUID.fromString(characterId)
        val request = PromoteMinorCharacter.RequestModel(
            preparedThemeId,
            preparedCharacterId
        )
        return threadTransformer.async {
            promoteMinorCharacter.invoke(request, promoteMinorCharacterOutputPort)
        }
    }
}