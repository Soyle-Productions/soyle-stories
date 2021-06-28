package com.soyle.stories.usecase.theme.listSymbolsInTheme

import com.soyle.stories.usecase.theme.SymbolItem
import java.util.*

class SymbolsInTheme(
    val themeId: UUID,
    val themeName: String,
    val symbols: List<SymbolItem>
)