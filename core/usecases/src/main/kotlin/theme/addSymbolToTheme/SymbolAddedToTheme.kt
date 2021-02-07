package com.soyle.stories.usecase.theme.addSymbolToTheme

import java.util.*

class SymbolAddedToTheme(
    val themeId: UUID,
    val symbolId: UUID,
    val symbolName: String
)