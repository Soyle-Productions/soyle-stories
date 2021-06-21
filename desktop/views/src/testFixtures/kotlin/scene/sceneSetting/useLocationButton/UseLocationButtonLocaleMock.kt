package com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton

import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButtonLocale
import javafx.beans.property.StringProperty
import tornadofx.stringProperty

class UseLocationButtonLocaleMock(
    override val useLocation: StringProperty = stringProperty("Use Location"),
    override val loading: StringProperty = stringProperty("Loading"),
    override val createLocation: StringProperty = stringProperty("Create Location"),
    override val allExistingLocationsInProjectHaveBeenUsed: StringProperty = stringProperty(
        "All Existing Locations in Project Have Been Used"
    )
) : UseLocationButtonLocale