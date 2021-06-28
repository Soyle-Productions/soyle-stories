package com.soyle.stories.usecase.theme.examineCentralConflictOfTheme

import java.util.*

interface ExamineCentralConflictOfTheme {

    suspend operator fun invoke(themeId: UUID, characterId: UUID?, outputPort: OutputPort)

    interface OutputPort {

        suspend fun centralConflictExamined(response: ExaminedCentralConflict)

    }

}