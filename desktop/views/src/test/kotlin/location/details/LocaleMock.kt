package com.soyle.stories.desktop.view.location.details

import com.soyle.stories.location.details.LocationDetailsLocale
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import tornadofx.stringProperty
import tornadofx.toProperty

class LocaleMock(
    override val description: SimpleStringProperty = SimpleStringProperty("Description"),
    override val scenesHostedInLocation: StringProperty = stringProperty("Scenes Hosted in Location"),
    override val hostScene: SimpleStringProperty = SimpleStringProperty("Host Scene"),
    override val loading: SimpleStringProperty = SimpleStringProperty("Loading"),
    override val createScene: SimpleStringProperty = SimpleStringProperty("Create Scene"),
    override val hostSceneInLocationInvitationMessage: StringProperty = stringProperty(
        """ 
            Nothing has ever or will ever happen here.  Sounds boring.  Why not spice this place up by adding a scene or five?
        """.trimIndent()
    ),
    override val allExistingScenesInProjectHaveBeenHosted: SimpleStringProperty = SimpleStringProperty(
        "All existing scenes in project have been hosted"
    ),
) : LocationDetailsLocale {

    override fun locationDetailsToolName(locationName: String): ObservableValue<String> = "Location Details - $locationName".toProperty()

}