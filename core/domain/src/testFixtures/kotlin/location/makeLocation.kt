package com.soyle.stories.domain.location

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.singleLine
import com.soyle.stories.domain.str
import com.soyle.stories.domain.validation.SingleNonBlankLine

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

fun locationName(value: String = "Location ${str()}"): SingleNonBlankLine = SingleNonBlankLine.create(singleLine(value))!!