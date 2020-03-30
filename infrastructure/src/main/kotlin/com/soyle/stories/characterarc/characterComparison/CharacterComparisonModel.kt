/**
 * Created by Brendan
 * Date: 3/3/2020
 * Time: 9:12 AM
 */
package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.common.bindImmutableList
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.rebind
import tornadofx.runLater

class CharacterComparisonModel : CharacterComparisonView, ItemViewModel<CharacterComparisonViewModel>(CharacterComparisonViewModel()) {

    val focusedCharacter = bind(CharacterComparisonViewModel::focusedCharacter)
    val characterOptions = bindImmutableList(CharacterComparisonViewModel::focusCharacterOptions)
    val subTools = bindImmutableList(CharacterComparisonViewModel::subTools)
    val availableCharactersToAdd = bindImmutableList(CharacterComparisonViewModel::availableCharactersToAdd)
    val isInvalid = bind(CharacterComparisonViewModel::isInvalid)

    val pageSelection = SimpleStringProperty("")

    override fun update(update: CharacterComparisonViewModel.() -> CharacterComparisonViewModel) {
        if (! Platform.isFxApplicationThread()) return runLater { update(update) }
        rebind { item = item.update() }
        println(item)
    }

}