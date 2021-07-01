package com.soyle.stories.theme.valueWeb.create

import javafx.beans.value.ObservableValue

interface CreateValueWebFormLocale {
    val name: ObservableValue<String>
    val nameCannotBeBlank: ObservableValue<String>
}