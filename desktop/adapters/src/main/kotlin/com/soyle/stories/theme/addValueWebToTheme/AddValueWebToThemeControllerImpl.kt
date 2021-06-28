package com.soyle.stories.theme.addValueWebToTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToTheme.RequestModel
import java.util.*

class AddValueWebToThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addValueWebToTheme: AddValueWebToTheme,
    private val addValueWebToThemeOutputPort: AddValueWebToTheme.OutputPort
) : AddValueWebToThemeController {

    override fun addValueWebToTheme(themeId: String, name: NonBlankString, onError: (Throwable) -> Unit) {
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            try {
                addValueWebToTheme.invoke(
                    RequestModel(
                        preparedThemeId,
                        name
                    ), addValueWebToThemeOutputPort
                )
            } catch (t: Throwable) { onError(t) }
        }
    }

    override fun addValueWebToThemeWithCharacter(
        themeId: String,
        name: NonBlankString,
        characterId: String,
        onError: (Throwable) -> Unit
    ) {
        val preparedThemeId = UUID.fromString(themeId)
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            try {
                addValueWebToTheme.invoke(
                    RequestModel(
                        preparedThemeId,
                        name,
                        CharacterId(preparedCharacterId)
                    ), addValueWebToThemeOutputPort
                )
            } catch (t: Throwable) { onError(t) }
        }
    }

}