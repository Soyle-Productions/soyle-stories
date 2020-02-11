package com.soyle.studio.workspace

import com.soyle.studio.project.Project
import java.net.URI

/**
 * Created by Brendan
 * Date: 2/11/2020
 * Time: 1:15 PM
 */
data class OpenProject(
	val projectId: Project.Id,
	val projectName: String,
	val lastKnownLocation: URI
)