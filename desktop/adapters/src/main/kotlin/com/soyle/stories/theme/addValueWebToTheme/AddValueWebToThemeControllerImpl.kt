package com.soyle.stories.theme.addValueWebToTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToTheme.RequestModel
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.util.*

class AddValueWebToThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addValueWebToTheme: AddValueWebToTheme,
    private val addValueWebToThemeOutputPort: AddValueWebToTheme.OutputPort
) : AddValueWebToThemeController {

    override fun addValueWebToTheme(
        themeId: String,
        name: NonBlankString,
        onError: (Throwable) -> Unit
    ): Deferred<ValueWebAddedToTheme> {
        val preparedThemeId = UUID.fromString(themeId)
        return addValueWebToTheme(
            RequestModel(
                preparedThemeId,
                name
            )
        )
    }

    override fun addValueWebToThemeWithCharacter(
        themeId: String,
        name: NonBlankString,
        characterId: String,
        onError: (Throwable) -> Unit
    ): Deferred<ValueWebAddedToTheme> {
        val preparedThemeId = UUID.fromString(themeId)
        val preparedCharacterId = UUID.fromString(characterId)
        return addValueWebToTheme(
            RequestModel(
                preparedThemeId,
                name,
                CharacterId(preparedCharacterId)
            )
        )
    }

    private fun addValueWebToTheme(request: RequestModel): Deferred<ValueWebAddedToTheme> {
        val deferred = CompletableDeferred<ValueWebAddedToTheme>()
        threadTransformer.async {
            addValueWebToTheme(request) {
                deferred.complete(it.addedValueWeb)
                addValueWebToThemeOutputPort.addedValueWebToTheme(it)
            }
        }
        return deferred
    }

}