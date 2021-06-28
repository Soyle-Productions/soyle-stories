package com.soyle.stories.usecase.theme.renameSymbol

import java.util.*

class RenamedSymbol(
    val themeId: UUID,
    val symbolId: UUID,
    val newName: String
)