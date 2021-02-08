package com.soyle.stories.theme.changeThemeDetails.changeThemeLine

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.changeThemeDetails.ChangeThemeLine
import java.util.*

class ChangeThemeLineControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val changeThemeLine: ChangeThemeLine,
    private val changeThemeLineOutput: ChangeThemeLine.OutputPort
) : ChangeThemeLineController {

    override fun changeThemeLine(themeId: String, themeLine: String) {
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            changeThemeLine.invoke(
                preparedThemeId,
                themeLine,
                changeThemeLineOutput
            )
        }
    }
}