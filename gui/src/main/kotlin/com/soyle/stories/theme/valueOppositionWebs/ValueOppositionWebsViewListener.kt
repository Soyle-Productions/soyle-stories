package com.soyle.stories.theme.valueOppositionWebs

import java.util.*

interface ValueOppositionWebsViewListener {

    fun getValidState()
    fun selectValueWeb(valueWebId: String)
    fun addOpposition(valueWebId: String)
    fun renameOppositionValue(oppositionId: String, name: String)

}