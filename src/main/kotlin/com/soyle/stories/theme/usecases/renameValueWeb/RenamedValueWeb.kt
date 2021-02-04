package com.soyle.stories.theme.usecases.renameValueWeb

import java.util.*

class RenamedValueWeb(
    val themeId: UUID,
    val valueWebId: UUID,
    val originalName: String,
    val newName: String
)