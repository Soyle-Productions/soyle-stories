package com.soyle.stories.theme.createThemeDialog

import com.soyle.stories.domain.validation.NonBlankString

interface CreateThemeDialogViewListener {

    fun getValidState()
    fun createTheme(name: NonBlankString)

}