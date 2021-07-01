package com.soyle.stories.theme.valueWeb.opposition.create

import javafx.beans.value.ObservableValue

interface CreateOppositionValueFormLocale {
    val name: ObservableValue<String>
    val nameCannotBeBlank: ObservableValue<String>
}