package com.soyle.stories.theme.addSymbolDialog

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.theme.themeList.SymbolListItemViewModel

class AddSymbolDialogViewModel(
    val characters: List<CharacterItemViewModel>,
    val locations: List<LocationItemViewModel>,
    val symbols: List<SymbolListItemViewModel>,
    val completed: Boolean
)