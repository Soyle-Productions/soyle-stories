package com.soyle.stories.theme.removeValueWebFromTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.RemoveValueWebFromTheme
import java.util.*

class RemoveValueWebFromThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeValueWebFromTheme: RemoveValueWebFromTheme,
    private val removeValueWebFromThemeOutputPort: RemoveValueWebFromTheme.OutputPort
) : RemoveValueWebFromThemeController {

    override fun removeValueWeb(valueWebId: String) {
        val preparedValueWebId = UUID.fromString(valueWebId)
        threadTransformer.async {
            removeValueWebFromTheme.invoke(
                preparedValueWebId,
                removeValueWebFromThemeOutputPort
            )
        }
    }

}