package com.soyle.stories.theme.createTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import java.util.*

class CreateThemeControllerImpl(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val createTheme: CreateTheme,
    private val createThemeOutputPort: CreateTheme.OutputPort
) : CreateThemeController {

    private val projectId = UUID.fromString(projectId)

    override fun createTheme(name: String, onError: (Throwable) -> Unit) {
        threadTransformer.async {
            try {
                createTheme.invoke(
                    projectId, name, createThemeOutputPort
                )
            } catch (t: Throwable) { onError(t) }
        }
    }

}