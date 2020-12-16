package com.soyle.stories.location

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.common.str

fun makeLocation(
    id: Location.Id = Location.Id(),
    projectId: Project.Id = Project.Id(),
    name: String = locationName(),
    description: String = ""
): Location =
    Location(
        id,
        projectId,
        name,
        description
    )

fun locationName() = "Location ${str()}"