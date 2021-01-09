package com.soyle.stories.location

import com.soyle.stories.common.SingleLine
import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.common.countLines
import com.soyle.stories.common.str
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project

fun makeLocation(
    id: Location.Id = Location.Id(),
    projectId: Project.Id = Project.Id(),
    name: SingleNonBlankLine = locationName(),
    description: String = ""
): Location =
    Location(
        id,
        projectId,
        name,
        description
    )

fun locationName(value: String = "Location ${str()}"): SingleNonBlankLine = SingleNonBlankLine.create(countLines(value) as SingleLine)!!