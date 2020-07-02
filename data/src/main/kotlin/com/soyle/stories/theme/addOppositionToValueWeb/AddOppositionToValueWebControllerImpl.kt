package com.soyle.stories.theme.addOppositionToValueWeb

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb
import java.util.*

class AddOppositionToValueWebControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addOppositionToValueWeb: AddOppositionToValueWeb,
    private val addOppositionToValueWebOutputPort: AddOppositionToValueWeb.OutputPort
) : AddOppositionToValueWebController {

    override fun addOpposition(valueWebId: String) {
        val preparedValueWebId = UUID.fromString(valueWebId)
        threadTransformer.async {
            addOppositionToValueWeb.invoke(
                preparedValueWebId,
                addOppositionToValueWebOutputPort
            )
        }
    }

}