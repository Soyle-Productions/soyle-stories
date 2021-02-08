package com.soyle.stories.theme.removeOppositionFromValueWeb

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWeb
import java.util.*

class RemoveOppositionFromValueWebControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeOppositionFromValueWeb: RemoveOppositionFromValueWeb,
    private val removeOppositionFromValueWebOutputPort: RemoveOppositionFromValueWeb.OutputPort
) : RemoveOppositionFromValueWebController {

    override fun removeOpposition(oppositionId: String, valueWebId: String) {
        val preparedOppositionId = UUID.fromString(oppositionId)
        val preparedValueWebId = UUID.fromString(valueWebId)
        threadTransformer.async {
            removeOppositionFromValueWeb.invoke(
                preparedOppositionId,
                preparedValueWebId,
                removeOppositionFromValueWebOutputPort
            )
        }
    }

}