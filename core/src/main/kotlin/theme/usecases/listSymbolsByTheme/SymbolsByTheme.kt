package com.soyle.stories.theme.usecases.listSymbolsByTheme

import com.soyle.stories.theme.usecases.SymbolItem
import com.soyle.stories.theme.usecases.ThemeItem

class SymbolsByTheme(val themes: List<Pair<ThemeItem, List<SymbolItem>>>) {

    fun isEmpty(): Boolean = true
}

val Pair<ThemeItem, List<SymbolItem>>.theme get() = first
val Pair<ThemeItem, List<SymbolItem>>.symbols get() = second