package com.soyle.stories.usecase.character.arc.planNewCharacterArc

import com.soyle.stories.usecase.theme.createTheme.CreatedTheme
import java.util.*

interface PlanNewCharacterArc {
    suspend operator fun invoke(
        characterId: UUID,
        name: String,
        outputPort: OutputPort
    )

    class ResponseModel(
        val createdCharacterArc: CreatedCharacterArc,
        val createdTheme: CreatedTheme
    )

    fun interface OutputPort {
        suspend fun characterArcPlanned(response: ResponseModel)
    }
}