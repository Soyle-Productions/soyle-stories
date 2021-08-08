package com.soyle.stories.theme.addValueWebToTheme

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme
import kotlinx.coroutines.Deferred

interface AddValueWebToThemeController {

    fun addValueWebToTheme(themeId: String, name: NonBlankString, onError: (Throwable) -> Unit): Deferred<ValueWebAddedToTheme>
    fun addValueWebToThemeWithCharacter(themeId: String, name: NonBlankString, characterId: String, onError: (Throwable) -> Unit): Deferred<ValueWebAddedToTheme>

}