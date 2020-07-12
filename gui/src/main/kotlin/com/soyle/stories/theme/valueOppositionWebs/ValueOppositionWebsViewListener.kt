package com.soyle.stories.theme.valueOppositionWebs

import java.util.*

interface ValueOppositionWebsViewListener {

    fun getValidState()
    fun selectValueWeb(valueWebId: String)
    fun addOpposition(valueWebId: String)
    fun removeOpposition(valueWebId: String, oppositionId: String)
    fun renameOppositionValue(oppositionId: String, name: String)
    fun renameValueWeb(valueWebId: String, name: String)
    fun removeSymbolicItem(oppositionId: String, itemId: String)

}