package com.soyle.stories.theme.characterValueComparison

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleObjectProperty
import tornadofx.onChange

class CharacterValueComparisonModel : Model<CharacterValueComparisonScope, CharacterValueComparisonViewModel>(CharacterValueComparisonScope::class) {

    val addCharacterButtonLabel = bind(CharacterValueComparisonViewModel::addCharacterButtonLabel)
    val openValueWebToolButtonLabel = bind(CharacterValueComparisonViewModel::openValueWebToolButtonLabel)
    val characters = bind(CharacterValueComparisonViewModel::characters)
    val availableCharacters = SimpleObjectProperty<List<CharacterItemViewModel>?>(null)
    val availableOppositionValues = SimpleObjectProperty<List<AvailableValueWebViewModel>?>(null)

    override fun viewModel(): CharacterValueComparisonViewModel? {
        return item?.copy(availableCharacters = availableCharacters.value, availableOppositionValues = availableOppositionValues.value)
    }

    init {
        itemProperty().onChange {
            availableCharacters.value = it?.availableCharacters
            availableOppositionValues.value = it?.availableOppositionValues
        }
    }

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}