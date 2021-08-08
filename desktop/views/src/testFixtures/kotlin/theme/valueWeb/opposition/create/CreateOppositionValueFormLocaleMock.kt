package com.soyle.stories.desktop.view.theme.valueWeb.opposition.create

import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueFormLocale
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import tornadofx.stringProperty

class CreateOppositionValueFormLocaleMock(
    override val name: StringProperty = stringProperty("Name"),
    override val nameCannotBeBlank: StringProperty = stringProperty("Name Cannot Be Blank"),
) : CreateOppositionValueFormLocale