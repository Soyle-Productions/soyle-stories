package com.soyle.stories.theme.usecases.changeThemeDetails

import java.util.*

class RenamedTheme(
    val themeId: UUID,
    val originalName: String,
    val newName: String
)