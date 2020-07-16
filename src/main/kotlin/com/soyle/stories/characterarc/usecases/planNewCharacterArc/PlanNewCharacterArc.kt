package com.soyle.stories.characterarc.usecases.planNewCharacterArc

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem
import com.soyle.stories.theme.usecases.ThemeItem
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import java.util.*

interface PlanNewCharacterArc {
    suspend operator fun invoke(
        characterId: UUID,
        name: String,
        outputPort: OutputPort
    )

    interface OutputPort {
        suspend fun themeNoted(response: CreatedTheme)
        suspend fun characterArcPlanned(response: CharacterArcItem)
    }
}