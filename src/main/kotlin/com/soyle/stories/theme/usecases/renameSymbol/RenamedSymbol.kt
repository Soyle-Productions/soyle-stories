package com.soyle.stories.theme.usecases.renameSymbol

import java.util.*

class RenamedSymbol(
    val themeId: UUID,
    val symbolId: UUID,
    val newName: String
)