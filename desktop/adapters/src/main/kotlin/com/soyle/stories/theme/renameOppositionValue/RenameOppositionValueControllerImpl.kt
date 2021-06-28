package com.soyle.stories.theme.renameOppositionValue

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.OppositionValueAlreadyHasName
import com.soyle.stories.usecase.theme.renameOppositionValue.RenameOppositionValue
import java.util.*

class RenameOppositionValueControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val renameOppositionValue: RenameOppositionValue,
    private val renameOppositionValueOutputPort: RenameOppositionValue.OutputPort
) : RenameOppositionValueController {

    override fun renameOpposition(oppositionValueId: String, newName: NonBlankString, onError: (Throwable) -> Unit) {
        val preparedOppositionValueId = UUID.fromString(oppositionValueId)
        threadTransformer.async {
            try {
                renameOppositionValue.invoke(
                    preparedOppositionValueId,
                    newName,
                    renameOppositionValueOutputPort
                )
            }
            catch (dup: OppositionValueAlreadyHasName) {}
            catch (t: Throwable) { onError(t) }
        }
    }

}