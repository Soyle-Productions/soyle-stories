package com.soyle.stories.usecase.theme.changeThemeDetails

import java.util.*

class RenamedTheme(
    val themeId: UUID,
    val originalName: String,
    val newName: String
)