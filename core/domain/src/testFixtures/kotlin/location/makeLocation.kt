package com.soyle.stories.domain.location

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.singleLine
import com.soyle.stories.domain.str
import com.soyle.stories.domain.validation.EntitySet
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.entitySetOf

fun makeLocation(
    id: Location.Id = Location.Id(),
    projectId: Project.Id = Project.Id(),
    name: SingleNonBlankLine = locationName(),
    description: String = "",
    hostedScenes: EntitySet<HostedScene> = entitySetOf()
): Location =
    Location(
        id,
        projectId,
        name,
        description,
        hostedScenes
    )

fun locationName(value: String = "Location ${str()}"): SingleNonBlankLine = SingleNonBlankLine.create(singleLine(value))!!