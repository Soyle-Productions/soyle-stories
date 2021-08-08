package com.soyle.stories.desktop.view.theme.valueWeb

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeController
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class AddValueWebToThemeControllerDouble(
    var onAddValueWebToTheme: (String, NonBlankString) -> Deferred<ValueWebAddedToTheme> = {_,_-> CompletableDeferred() }
) : AddValueWebToThemeController {

    override fun addValueWebToTheme(themeId: String, name: NonBlankString, onError: (Throwable) -> Unit): Deferred<ValueWebAddedToTheme> {
        return onAddValueWebToTheme(themeId, name)
    }

    override fun addValueWebToThemeWithCharacter(
        themeId: String,
        name: NonBlankString,
        characterId: String,
        onError: (Throwable) -> Unit
    ): Deferred<ValueWebAddedToTheme> {
        return onAddValueWebToTheme(themeId, name)
    }
}