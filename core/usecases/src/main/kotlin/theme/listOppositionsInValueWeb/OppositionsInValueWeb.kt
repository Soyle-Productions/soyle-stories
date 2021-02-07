package com.soyle.stories.usecase.theme.listOppositionsInValueWeb

import java.util.*

class OppositionsInValueWeb(val oppositions: List<OppositionValueWithSymbols>) {

    fun isEmpty() = true

}

open class OppositionValueItem(val oppositionValueId: UUID, val oppositionValueName: String)

class OppositionValueWithSymbols(
    oppositionValueId: UUID,
    oppositionValueName: String,
    val symbols: List<SymbolicItem>
) : OppositionValueItem(oppositionValueId, oppositionValueName) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OppositionValueWithSymbols

        if (oppositionValueId != other.oppositionValueId) return false
        if (oppositionValueName != other.oppositionValueName) return false
        if (symbols != other.symbols) return false

        return true
    }

    override fun hashCode(): Int {
        var result = oppositionValueId.hashCode()
        result = 31 * result + oppositionValueName.hashCode()
        result = 31 * result + symbols.hashCode()
        return result
    }
}

data class SymbolicItem(val symbolicId: UUID, val name: String)