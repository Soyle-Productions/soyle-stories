package com.soyle.stories.workspace.repositories

import com.soyle.stories.domain.project.Project

interface ProjectRepository {
    suspend fun getProjectAtLocation(location: String): Project?
}