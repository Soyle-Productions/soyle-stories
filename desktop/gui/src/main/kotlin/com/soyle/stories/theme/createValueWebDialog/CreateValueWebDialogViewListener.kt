package com.soyle.stories.theme.createValueWebDialog

import com.soyle.stories.domain.validation.NonBlankString

interface CreateValueWebDialogViewListener {

    fun getValidState()
    fun createValueWeb(themeId: String, name: NonBlankString)
    fun createValueWebAndLinkCharacter(themeId: String, name: NonBlankString, characterId: String)

}