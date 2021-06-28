package com.soyle.stories.theme.changeThemeDetails.renameTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.changeThemeDetails.RenameTheme
import java.util.*

class RenameThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val renameTheme: RenameTheme,
    private val renameThemeOutputPort: RenameTheme.OutputPort
) : RenameThemeController {

    override fun renameTheme(themeId: String, newName: NonBlankString) {
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            renameTheme.invoke(
                preparedThemeId,
                newName,
                renameThemeOutputPort
            )
        }
    }

}