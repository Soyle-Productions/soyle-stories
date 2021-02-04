/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 11:10 AM
 */
package com.soyle.stories.project.repositories

import com.soyle.stories.entities.Project

interface ProjectRepository {
    suspend fun addNewProject(project: Project)
}