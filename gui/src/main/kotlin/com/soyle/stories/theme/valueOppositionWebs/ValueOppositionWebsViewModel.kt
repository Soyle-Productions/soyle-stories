package com.soyle.stories.theme.valueOppositionWebs

data class ValueOppositionWebsViewModel(
    val valueWebs: List<ValueWebItemViewModel>,
    val selectedValueWeb: ValueWebItemViewModel?,
    val oppositionValues: List<OppositionValueViewModel>,
    val errorMessage: String?,
    val errorSource: String?
)

class ValueWebItemViewModel(val valueWebId: String, val valueWebName: String)
data class OppositionValueViewModel(val oppositionValueId: String, val oppositionValueName: String, val isNew: Boolean, val symbolicItems: List<SymbolicItemViewModel>)
data class SymbolicItemViewModel(val itemId: String, val itemName: String)