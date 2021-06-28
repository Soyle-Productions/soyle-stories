package com.soyle.stories.theme.changeThemeDetails.renameTheme

import com.soyle.stories.domain.validation.NonBlankString

interface RenameThemeController {

    fun renameTheme(themeId: String, newName: NonBlankString)

}