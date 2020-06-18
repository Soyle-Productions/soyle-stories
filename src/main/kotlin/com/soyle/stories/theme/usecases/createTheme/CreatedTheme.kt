package com.soyle.stories.theme.usecases.createTheme

import java.util.*

class CreatedTheme(
    val projectId: UUID,
    val themeId: UUID,
    val themeName: String
)