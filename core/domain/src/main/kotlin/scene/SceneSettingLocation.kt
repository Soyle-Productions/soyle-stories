package com.soyle.stories.domain.scene

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.Location

class SceneSettingLocation(override val id: Location.Id, val locationName: String) : Entity<Location.Id> {
    constructor(location: Location) : this(location.id, location.name.value)

}