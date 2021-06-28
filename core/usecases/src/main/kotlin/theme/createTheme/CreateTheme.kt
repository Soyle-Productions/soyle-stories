package com.soyle.stories.usecase.theme.createTheme

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addSymbolToTheme.AddSymbolToTheme
import java.util.*

interface CreateTheme {

    class RequestModel(
        val projectId: UUID,
        val themeName: NonBlankString,
        val firstSymbolName: NonBlankString? = null
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    interface OutputPort: AddSymbolToTheme.OutputPort {
        suspend fun themeCreated(response: CreatedTheme)
    }

}