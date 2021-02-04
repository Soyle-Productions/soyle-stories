package com.soyle.stories.theme.usecases.addSymbolToTheme

import java.util.*

class SymbolAddedToTheme(
    val themeId: UUID,
    val symbolId: UUID,
    val symbolName: String
)