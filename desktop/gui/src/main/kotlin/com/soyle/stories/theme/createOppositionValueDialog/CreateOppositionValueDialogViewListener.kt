package com.soyle.stories.theme.createOppositionValueDialog

import com.soyle.stories.domain.validation.NonBlankString

interface CreateOppositionValueDialogViewListener {

    fun getValidState()

    fun createOppositionValue(valueWebId: String, name: NonBlankString, linkedCharacterId: String)

}