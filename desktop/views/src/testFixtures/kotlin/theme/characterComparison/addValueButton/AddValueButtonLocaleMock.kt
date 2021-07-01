package com.soyle.stories.desktop.view.theme.characterComparison.addValueButton

import com.soyle.stories.theme.characterValueComparison.components.addValueButton.AddValueButtonLocale
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import tornadofx.stringProperty

class AddValueButtonLocaleMock(
    override val addValue: StringProperty = stringProperty("Add Value"),
    override val loading: StringProperty = stringProperty("Loading..."),
    override val createNewValueWeb: StringProperty = stringProperty("Create New Value Web"),
    override val themeHasNoValueWebs: StringProperty = stringProperty("Theme Has No Value Webs"),
    override val createOppositionValue: StringProperty = stringProperty("Create Opposition Value"),
) : AddValueButtonLocale {
}