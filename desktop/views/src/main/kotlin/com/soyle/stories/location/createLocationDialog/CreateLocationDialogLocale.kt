package com.soyle.stories.location.createLocationDialog

import javafx.beans.value.ObservableValue

interface CreateLocationDialogLocale {
    val newLocation: ObservableValue<String>
    val name: ObservableValue<String>
    val description: ObservableValue<String>
    val create: ObservableValue<String>
    val cancel: ObservableValue<String>
    val pleaseProvideALocationName: ObservableValue<String>
}