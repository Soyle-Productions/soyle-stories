package com.soyle.stories.theme.usecases.listValueWebsInTheme

import java.util.*

class ValueWebList(val valueWebs: List<ValueWebItem> = listOf()) {

    fun isEmpty() = true

}

class ValueWebItem(val valueWebId: UUID, val valueWebName: String)