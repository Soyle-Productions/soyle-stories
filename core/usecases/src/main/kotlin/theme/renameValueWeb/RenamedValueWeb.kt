package com.soyle.stories.usecase.theme.renameValueWeb

import java.util.*

class RenamedValueWeb(
    val themeId: UUID,
    val valueWebId: UUID,
    val originalName: String,
    val newName: String
)