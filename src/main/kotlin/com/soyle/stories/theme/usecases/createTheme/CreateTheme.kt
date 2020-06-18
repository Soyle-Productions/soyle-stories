package com.soyle.stories.theme.usecases.createTheme

import com.soyle.stories.entities.Project
import java.util.*

interface CreateTheme {

    suspend operator fun invoke(projectId: UUID, name: String, output: OutputPort)

    interface OutputPort {
        suspend fun themeCreated(response: CreatedTheme)
    }

}