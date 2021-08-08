package com.soyle.stories.desktop.view.theme.characterComparison.addValueButton

import com.soyle.stories.desktop.view.theme.characterComparison.doubles.AddSymbolicItemToOppositionControllerDouble
import com.soyle.stories.desktop.view.theme.characterComparison.doubles.ListAvailableOppositionValuesForCharacterInThemeControllerDouble
import com.soyle.stories.desktop.view.theme.valueWeb.create.CreateValueWebFormFactory
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.CreateOppositionValueFormFactory
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.theme.characterValueComparison.components.addValueButton.AddValueButton
import com.soyle.stories.theme.valueWeb.opposition.list.ListAvailableOppositionValuesForCharacterInThemeController

class AddValueButtonFactory(
    var listAvailableOppositionValuesForCharacterInThemeController: ListAvailableOppositionValuesForCharacterInThemeControllerDouble = ListAvailableOppositionValuesForCharacterInThemeControllerDouble()
) : AddValueButton.Factory {

    override fun invoke(themeId: Theme.Id, characterId: Character.Id): AddValueButton =
        AddValueButton(
            themeId,
            characterId,
            AddValueButtonLocaleMock(),
            listAvailableOppositionValuesForCharacterInThemeController,
            AddSymbolicItemToOppositionControllerDouble(),
            CreateValueWebFormFactory(),
            CreateOppositionValueFormFactory()
        )
}