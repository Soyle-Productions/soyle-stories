package com.soyle.stories.theme.renameValueWeb

interface RenameValueWebController {

    fun renameValueWeb(valueWebId: String, name: String, onError: (Throwable) -> Unit)

}