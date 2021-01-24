package com.soyle.stories.project

import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationScope
import java.util.*

fun makeProjectScope(
    projectId: UUID = UUID.randomUUID(),
    name: String = "",
    location: String = ""
) = ProjectScope(
    ApplicationScope(), ProjectFileViewModel(projectId, name, location)
)