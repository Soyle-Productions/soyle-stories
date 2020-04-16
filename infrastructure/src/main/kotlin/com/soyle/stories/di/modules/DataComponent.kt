package com.soyle.stories.di.modules

import com.soyle.stories.characterarc.repositories.CharacterArcRepository
import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.*
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.project.repositories.ProjectRepository
import com.soyle.stories.repositories.*
import com.soyle.stories.theme.Context
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import tornadofx.Component
import tornadofx.ScopedInstance

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 4:05 PM
 */
class DataComponent : Component(), ScopedInstance {

    val workerId: String = System.getProperty("user.name")

    val workspaceRepository: WorkspaceRepository = WorkspaceRepositoryImpl()

    private val _projectFileRepository = ProjectFileRepository()

    val projectRepository: ProjectRepository = _projectFileRepository

    val projectFileRepository: com.soyle.stories.workspace.repositories.ProjectRepository = _projectFileRepository

    val fileRepository: FileRepository = _projectFileRepository

    val layoutRepository: LayoutRepository = LayoutRepositoryImpl()

    val characterRepository: CharacterRepositoryImpl = CharacterRepositoryImpl()

    val characterArcRepository = CharacterArcRepositoryImpl()
    val themeRepository = ThemeRepositoryImpl()

    val characterArcSectionRepository = CharacterArcSectionRepositoryImpl()

    val locationRepository = LocationRepositoryImpl()

    val context = ContextDouble(this)
}

class ContextDouble(dataComponent: DataComponent) : Context, com.soyle.stories.layout.Context {
    override val characterArcRepository: com.soyle.stories.theme.repositories.CharacterArcRepository = dataComponent.characterArcRepository
    override val characterArcSectionRepository: CharacterArcSectionRepository = dataComponent.characterArcSectionRepository
    override val characterRepository: com.soyle.stories.theme.repositories.CharacterRepository = dataComponent.characterRepository
    override val themeRepository: com.soyle.stories.theme.repositories.ThemeRepository = dataComponent.themeRepository
    override val layoutRepository: LayoutRepository = dataComponent.layoutRepository
}