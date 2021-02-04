/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 9:23 AM
 */
package com.soyle.stories.workspace.repositories

import com.soyle.stories.entities.Project

interface ProjectRepository {
    suspend fun getProjectAtLocation(location: String): Project?
}