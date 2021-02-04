package com.soyle.stories.theme.createOppositionValueDialog

interface CreateOppositionValueDialogViewListener {

    fun getValidState()

    fun createOppositionValue(valueWebId: String, name: String, linkedCharacterId: String)

}