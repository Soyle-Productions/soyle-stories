package com.soyle.stories.desktop.view.theme.valueWeb.create

import com.soyle.stories.theme.valueWeb.create.CreateValueWebFormLocale
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import tornadofx.stringProperty

class CreateValueWebFormLocaleMock(
    override val name: StringProperty = stringProperty("Name"),
    override val nameCannotBeBlank: StringProperty = stringProperty("Name Cannot Be Blank")
) : CreateValueWebFormLocale