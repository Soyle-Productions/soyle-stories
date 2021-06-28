package com.soyle.stories.theme.renameSymbol

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.SymbolAlreadyHasName

interface RenameSymbolController {
    fun renameSymbol(symbolId: String, name: NonBlankString, onError: (SymbolAlreadyHasName) -> Unit)
}