package com.soyle.stories.theme.renameOppositionValue

interface RenameOppositionValueController {

    fun renameOpposition(oppositionValueId: String, newName: String, onError: (Throwable) -> Unit)

}