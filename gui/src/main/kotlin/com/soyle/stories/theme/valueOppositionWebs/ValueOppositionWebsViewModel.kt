package com.soyle.stories.theme.valueOppositionWebs

data class ValueOppositionWebsViewModel(
    val valueWebs: List<ValueWebItemViewModel>
)

class ValueWebItemViewModel(val valueWebId: String, val valueWebName: String)