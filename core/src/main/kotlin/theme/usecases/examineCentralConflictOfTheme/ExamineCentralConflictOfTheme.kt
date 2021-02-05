package com.soyle.stories.theme.usecases.examineCentralConflictOfTheme

import java.util.*

interface ExamineCentralConflictOfTheme {

    suspend operator fun invoke(themeId: UUID, characterId: UUID?, outputPort: OutputPort)

    interface OutputPort {

        suspend fun centralConflictExamined(response: ExaminedCentralConflict)

    }

}