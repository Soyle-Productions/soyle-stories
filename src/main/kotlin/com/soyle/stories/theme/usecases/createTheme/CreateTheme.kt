package com.soyle.stories.theme.usecases.createTheme

import com.soyle.stories.entities.Project
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import java.util.*

interface CreateTheme {

    class RequestModel(
        val projectId: UUID,
        val themeName: String,
        val firstSymbolName: String? = null
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    interface OutputPort: AddSymbolToTheme.OutputPort {
        suspend fun themeCreated(response: CreatedTheme)
    }

}