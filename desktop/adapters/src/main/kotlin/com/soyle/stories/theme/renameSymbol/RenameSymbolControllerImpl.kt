package com.soyle.stories.theme.renameSymbol

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.SymbolAlreadyHasName
import com.soyle.stories.usecase.theme.renameSymbol.RenameSymbol
import java.util.*

class RenameSymbolControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val renameSymbol: RenameSymbol,
    private val renameSymbolOutputPort: RenameSymbol.OutputPort
) : RenameSymbolController {

    override fun renameSymbol(symbolId: String, name: NonBlankString, onError: (SymbolAlreadyHasName) -> Unit) {
        val preparedSymbolId  = UUID.fromString(symbolId)
        threadTransformer.async {
            try {
                renameSymbol.invoke(
                    preparedSymbolId,
                    name,
                    renameSymbolOutputPort
                )
            }
            catch (duplicate: SymbolAlreadyHasName) { onError(duplicate) }
        }
    }

}