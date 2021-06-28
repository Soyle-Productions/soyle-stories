package com.soyle.stories.usecase.theme.removeSymbolFromTheme

import java.util.*

class SymbolRemovedFromTheme(
    val themeId: UUID,
    val symbolId: UUID,
    val symbolName: String
)