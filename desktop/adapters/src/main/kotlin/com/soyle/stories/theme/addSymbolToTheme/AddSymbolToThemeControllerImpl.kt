package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addSymbolToTheme.AddSymbolToTheme
import java.util.*

class AddSymbolToThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addSymbolToTheme: AddSymbolToTheme,
    private val addSymbolToThemeOutputPort: AddSymbolToTheme.OutputPort
) : AddSymbolToThemeController {

    override fun addSymbolToTheme(themeId: String, name: NonBlankString, onError: (Throwable) -> Unit) {
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