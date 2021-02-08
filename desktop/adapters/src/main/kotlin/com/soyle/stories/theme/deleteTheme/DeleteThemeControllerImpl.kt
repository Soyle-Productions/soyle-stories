package com.soyle.stories.theme.deleteTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.deleteTheme.DeleteTheme
import java.util.*

class DeleteThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val deleteTheme: DeleteTheme,
    private val deleteThemeOutputPort: DeleteTheme.OutputPort
) : DeleteThemeController {

    override fun deleteTheme(themeId: String) {
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            deleteTheme.invoke(
                preparedThemeId,
                deleteThemeOutputPort
            )
        }
    }

}