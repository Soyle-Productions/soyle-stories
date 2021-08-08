package com.soyle.stories.usecase.framework

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble

abstract class CrossDomainTest {

    val projectRepository: ProjectRepository = object : ProjectRepository {
        val projectsById = mutableMapOf<Project.Id, Project>()
        override suspend fun addNewProject(project: Project) {
            projectsById[project.id] = project
        }
    }
    val characterRepository: CharacterRepositoryDouble = CharacterRepositoryDouble()
    val themeRepository: ThemeRepositoryDouble = ThemeRepositoryDouble()
    val characterArcRepository: CharacterArcRepositoryDouble = CharacterArcRepositoryDouble()

}