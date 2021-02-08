package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.changeThematicSectionValue.ChangeThematicSectionValue
import java.util.*

class ChangeThematicSectionValueController(
    private val threadTransformer: ThreadTransformer,
    private val useCase: ChangeThematicSectionValue,
    private val outputPort: ChangeThematicSectionValue.OutputPort
) {

    fun changeThematicSectionValue(sectionId: String, value: String) {
        executeUseCaseInBackground(
            UUID.fromString(sectionId),
            value
        )
    }

    private fun executeUseCaseInBackground(sectionId: UUID, value: String)
    {
        threadTransformer.async {
            executeUseCase(sectionId, value)
        }
    }

    private suspend fun executeUseCase(sectionId: UUID, value: String) {
        useCase.invoke(
            sectionId,
            value,
            outputPort
        )
    }

}