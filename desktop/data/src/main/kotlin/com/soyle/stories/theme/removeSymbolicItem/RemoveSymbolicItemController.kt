package com.soyle.stories.theme.removeSymbolicItem

interface RemoveSymbolicItemController {

    fun removeItemFromOpposition(oppositionId: String, itemId: String, onError: (Throwable) -> Unit)

}