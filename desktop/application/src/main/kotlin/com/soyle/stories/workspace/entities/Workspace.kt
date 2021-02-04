/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 11:43 AM
 */
package com.soyle.stories.workspace.entities

import arrow.core.Either
import arrow.core.right
import com.soyle.stories.entities.Project
import com.soyle.stories.workspace.valueobjects.ProjectFile

class Workspace(
    val workerId: String,
    val openProjects: List<ProjectFile>
) {

    fun removeProject(projectId: Project.Id): Either<Exception, Workspace>
    {
        return Workspace(workerId, openProjects.filter { it.projectId != projectId }).right()
    }

    fun addProject(projectFile: ProjectFile): Either<Exception, Workspace>
    {
        return Workspace(workerId, openProjects + projectFile).right()
    }

}