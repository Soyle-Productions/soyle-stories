package com.soyle.stories.theme.usecases.removeSymbolFromTheme

import java.util.*

class SymbolRemovedFromTheme(
    val themeId: UUID,
    val symbolId: UUID,
    val symbolName: String
)