package com.soyle.stories.theme.usecases.listOppositionsInValueWeb

import com.soyle.stories.theme.usecases.SymbolItem
import java.util.*

class OppositionsInValueWeb(val oppositions: List<OppositionValueItem>) {

    fun isEmpty() = true

}

data class OppositionValueItem(val oppositionValueId: UUID, val oppositionValueName: String, val symbols: List<SymbolicItem>)
data class SymbolicItem(val symbolicId: UUID, val name: String)