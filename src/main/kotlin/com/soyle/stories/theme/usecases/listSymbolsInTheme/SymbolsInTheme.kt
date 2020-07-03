package com.soyle.stories.theme.usecases.listSymbolsInTheme

import com.soyle.stories.theme.usecases.SymbolItem
import java.util.*

class SymbolsInTheme(
    val themeId: UUID,
    val themeName: String,
    val symbols: List<SymbolItem>
)