package com.soyle.stories.theme.removeSymbolFromTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import java.util.*

class RemoveSymbolFromThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeSymbolFromTheme: RemoveSymbolFromTheme,
    private val removeSymbolFromThemeOutputPort: RemoveSymbolFromTheme.OutputPort
) : RemoveSymbolFromThemeController {

    override fun removeSymbolFromTheme(symbolId: String) {
        val preparedSymbolId = UUID.fromString(symbolId)
        threadTransformer.async {
            removeSymbolFromTheme.invoke(
                preparedSymbolId,
                removeSymbolFromThemeOutputPort
            )
        }
    }

}