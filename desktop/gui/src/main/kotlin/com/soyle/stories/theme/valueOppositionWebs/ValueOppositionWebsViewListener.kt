package com.soyle.stories.theme.valueOppositionWebs

import com.soyle.stories.domain.validation.NonBlankString

interface ValueOppositionWebsViewListener {

    fun getValidState()
    fun selectValueWeb(valueWebId: String)
    fun addOpposition(valueWebId: String)
    fun removeOpposition(valueWebId: String, oppositionId: String)
    fun renameOppositionValue(oppositionId: String, name: NonBlankString)
    fun renameValueWeb(valueWebId: String, name: NonBlankString)
    fun removeSymbolicItem(oppositionId: String, itemId: String)

}