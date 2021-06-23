package com.soyle.stories.desktop.view.location.create

import com.soyle.stories.location.createLocationDialog.CreateLocationDialogLocale
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import tornadofx.stringProperty

class CreateLocationDialogLocaleMock(
    override val newLocation: StringProperty = stringProperty("New Location"),
    override val name: StringProperty = stringProperty("Name"),
    override val description: StringProperty = stringProperty("Description"),
    override val create: StringProperty = stringProperty("Create"),
    override val cancel: StringProperty = stringProperty("Cancel"),
    override val pleaseProvideALocationName: StringProperty = stringProperty("Cancel"),
) : CreateLocationDialogLocale