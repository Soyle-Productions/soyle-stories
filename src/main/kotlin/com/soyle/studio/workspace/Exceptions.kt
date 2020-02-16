package com.soyle.studio.workspace

import com.soyle.studio.project.Project
import java.util.*

/**
 * Created by Brendan
 * Date: 2/11/2020
 * Time: 1:29 PM
 */
class ProjectAlreadyOpen(val workspaceId: WorkSpace.Id, val projectId: Project.Id) : Exception()
data class ProjectIsNotOpen(val projectId: Project.Id) : Exception()