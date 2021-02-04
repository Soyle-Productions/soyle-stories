package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import java.util.*

class AddSymbolToThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addSymbolToTheme: AddSymbolToTheme,
    private val addSymbolToThemeOutputPort: AddSymbolToTheme.OutputPort
) : AddSymbolToThemeController {

    override fun addSymbolToTheme(themeId: String, name: String, onError: (Throwable) -> Unit) {
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            try {
                addSymbolToTheme.invoke(
                    preparedThemeId,
                    name,
                    addSymbolToThemeOutputPort
                )
            } catch (t: Throwable) { onError(t) }
        }
    }

}