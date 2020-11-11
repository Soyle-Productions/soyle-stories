package com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.changeThemeDetails.ChangeThematicRevelation
import java.util.*

class ChangeThematicRevelationControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val changeThematicRevelation: ChangeThematicRevelation,
    private val changeThematicRevelationOutput: ChangeThematicRevelation.OutputPort
) : ChangeThematicRevelationController {

    override fun changeThematicRevelation(themeId: String, revelation: String) {
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            changeThematicRevelation.invoke(
                preparedThemeId,
                revelation,
                changeThematicRevelationOutput
            )
        }
    }

}