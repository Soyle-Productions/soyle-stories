package com.soyle.stories.theme.renameOppositionValue

import com.soyle.stories.domain.validation.NonBlankString

interface RenameOppositionValueController {

    fun renameOpposition(oppositionValueId: String, newName: NonBlankString, onError: (Throwable) -> Unit)

}