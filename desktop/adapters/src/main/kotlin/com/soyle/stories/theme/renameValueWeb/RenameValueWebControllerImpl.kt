package com.soyle.stories.theme.renameValueWeb

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.ValueWebAlreadyHasName
import com.soyle.stories.usecase.theme.renameValueWeb.RenameValueWeb
import java.util.*

class RenameValueWebControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val renameValueWeb: RenameValueWeb,
    private val renameValueWebOutputPort: RenameValueWeb.OutputPort
) : RenameValueWebController {

    override fun renameValueWeb(valueWebId: String, name: NonBlankString, onError: (Throwable) -> Unit) {
        val preparedValueWebId = UUID.fromString(valueWebId)
        threadTransformer.async {
            try {
                renameValueWeb(
                    preparedValueWebId,
                    name,
                    renameValueWebOutputPort
                )
            }
            catch (dup: ValueWebAlreadyHasName) {}
            catch (t: Throwable) { onError(t) }
        }
    }

}