package com.soyle.stories.usecase.theme.listSymbolsByTheme

import com.soyle.stories.usecase.theme.SymbolItem
import com.soyle.stories.usecase.theme.ThemeItem


class SymbolsByTheme(val themes: List<Pair<ThemeItem, List<SymbolItem>>>) {

    fun isEmpty(): Boolean = true
}

val Pair<ThemeItem, List<SymbolItem>>.theme get() = first
val Pair<ThemeItem, List<SymbolItem>>.symbols get() = second