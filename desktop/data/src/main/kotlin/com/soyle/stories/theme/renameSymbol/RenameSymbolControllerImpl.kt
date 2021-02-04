package com.soyle.stories.theme.renameSymbol

import com.soyle.stories.common.DuplicateOperationException
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.SymbolAlreadyHasName
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol
import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol
import java.util.*

class RenameSymbolControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val renameSymbol: RenameSymbol,
    private val renameSymbolOutputPort: RenameSymbol.OutputPort
) : RenameSymbolController {

    override fun renameSymbol(symbolId: String, name: String, onError: (SymbolAlreadyHasName) -> Unit) {
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