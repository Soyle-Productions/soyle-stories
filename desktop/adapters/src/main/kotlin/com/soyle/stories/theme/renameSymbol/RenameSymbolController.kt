package com.soyle.stories.theme.renameSymbol

import com.soyle.stories.theme.SymbolAlreadyHasName

interface RenameSymbolController {
    fun renameSymbol(symbolId: String, name: String, onError: (SymbolAlreadyHasName) -> Unit)
}