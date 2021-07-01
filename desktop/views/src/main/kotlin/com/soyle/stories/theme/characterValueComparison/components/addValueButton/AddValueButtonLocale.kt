package com.soyle.stories.theme.characterValueComparison.components.addValueButton

import javafx.beans.value.ObservableValue

interface AddValueButtonLocale {
    val addValue: ObservableValue<String>
    val loading: ObservableValue<String>
    val createNewValueWeb: ObservableValue<String>
    val themeHasNoValueWebs: ObservableValue<String>
    val createOppositionValue: ObservableValue<String>
}