package com.soyle.stories.theme.renameValueWeb

import com.soyle.stories.domain.validation.NonBlankString

interface RenameValueWebController {

    fun renameValueWeb(valueWebId: String, name: NonBlankString, onError: (Throwable) -> Unit)

}