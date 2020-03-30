package com.soyle.stories.di.modules

import com.soyle.stories.characterarc.repositories.CharacterArcRepository
import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.*
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.project.repositories.ProjectRepository
import com.soyle.stories.repositories.ProjectFileRepository
import com.soyle.stories.theme.Context
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.workspace.entities.Workspace
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

    val workspaceRepository: WorkspaceRepository = object : WorkspaceRepository {
        val workspaces = mutableMapOf<String, Workspace>()

        override suspend fun addNewWorkspace(workspace: Workspace) {
            workspaces[workspace.workerId] = workspace
        }

        override suspend fun getWorkSpaceForWorker(workerId: String): Workspace? = workspaces[workerId]

        override suspend fun updateWorkspace(workspace: Workspace) {
            workspaces[workspace.workerId] = workspace
        }
    }

    private val _projectFileRepository = ProjectFileRepository()

    val projectRepository: ProjectRepository = _projectFileRepository

    val projectFileRepository: com.soyle.stories.workspace.repositories.ProjectRepository = _projectFileRepository

    val fileRepository: FileRepository = _projectFileRepository

    val layoutRepository: LayoutRepository = object : LayoutRepository {
        var layout: Layout? = null

        override suspend fun getLayoutForProject(projectId: Project.Id): Layout? = layout

        override suspend fun saveLayout(layout: Layout) { this.layout = layout }

        override fun getLayoutContainingTool(toolId: Tool.Id): Layout? = layout

        override fun getLayoutsContainingToolIds(toolIds: Set<Tool.Id>): List<Layout> = listOfNotNull(layout)
    }

    val characterRepository: CharacterRepositoryDouble = CharacterRepositoryDouble()

    val characterArcRepository = CharacterArcRepositoryDouble()
    val themeRepository = ThemeRepositoryDouble()

    val characterArcSectionRepository: CharacterArcSectionRepository = object : CharacterArcSectionRepository {
        val arcSections = mutableMapOf<CharacterArcSection.Id, CharacterArcSection>()
        override suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection? {
            return arcSections[characterArcSectionId]
        }

        override suspend fun removeArcSections(sections: List<CharacterArcSection>) {
            sections.forEach {
                arcSections.remove(it.id)
            }
        }

        override suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection) {
            arcSections[characterArcSection.id] = characterArcSection
        }

        override suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>) {
            arcSections.putAll(characterArcSections.map { it.id to it })
        }

        override suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection> {
            return arcSections.filterValues { it.characterId == characterId }.values.toList()
        }

        override suspend fun getCharacterArcSectionsForCharacterInTheme(
            characterId: Character.Id,
            themeId: Theme.Id
        ): List<CharacterArcSection> {
            return arcSections.filterValues { it.themeId == themeId && it.characterId == characterId }.values.toList()
        }

        override suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection> {
            return arcSections.filterKeys { it in characterArcSectionIds }.values.toList()
        }

        override suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection> {
            return arcSections.filterValues { it.themeId == themeId }.values.toList()
        }
    }

    val context = ContextDouble(this)
}

class ContextDouble(dataComponent: DataComponent) : Context, com.soyle.stories.layout.Context {
    override val characterArcRepository: com.soyle.stories.theme.repositories.CharacterArcRepository = dataComponent.characterArcRepository
    override val characterArcSectionRepository: CharacterArcSectionRepository = dataComponent.characterArcSectionRepository
    override val characterRepository: com.soyle.stories.theme.repositories.CharacterRepository = dataComponent.characterRepository
    override val themeRepository: com.soyle.stories.theme.repositories.ThemeRepository = dataComponent.themeRepository
    override val layoutRepository: LayoutRepository = dataComponent.layoutRepository
}

class CharacterArcRepositoryDouble : CharacterArcRepository, com.soyle.stories.theme.repositories.CharacterArcRepository {
    val characterArcs = mutableMapOf<Pair<Character.Id, Theme.Id>, CharacterArc>()
    override suspend fun addNewCharacterArc(characterArc: CharacterArc) {
        characterArcs[characterArc.characterId to characterArc.themeId] = characterArc
    }

    override suspend fun getCharacterArcByCharacterAndThemeId(
        characterId: Character.Id,
        themeId: Theme.Id
    ): CharacterArc? = characterArcs[characterId to themeId]

    override suspend fun listAllCharacterArcsInProject(projectId: Project.Id): List<CharacterArc> = characterArcs.values.toList()
    override suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc> = characterArcs.values.filter { it.themeId == themeId }
    override suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {
        characterArcs.remove(characterId to themeId)
    }
}

class CharacterRepositoryDouble : CharacterRepository, com.soyle.stories.character.repositories.CharacterRepository, com.soyle.stories.theme.repositories.CharacterRepository {

    val characters = mutableMapOf<Character.Id, Character>()
    override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]

    override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> = characters.values.toList()
    override suspend fun addNewCharacter(character: Character) {
        characters[character.id] = character
    }

    override suspend fun deleteCharacterWithId(characterId: Character.Id) {
        characters.remove(characterId)
    }
}
class ThemeRepositoryDouble : ThemeRepository, com.soyle.stories.theme.repositories.ThemeRepository, com.soyle.stories.character.repositories.ThemeRepository {
    val themes = mutableMapOf<Theme.Id, Theme>()
    override suspend fun addNewTheme(theme: Theme) {
        themes[theme.id] = theme
    }

    override suspend fun getThemeById(themeId: Theme.Id): Theme? = themes[themeId]
    override suspend fun updateTheme(theme: Theme) {
        themes[theme.id] = theme
    }

    override suspend fun deleteThemes(themes: List<Theme>) {
        themes.forEach {
            this.themes.remove(it.id)
        }
    }

    override suspend fun deleteTheme(theme: Theme) {
        deleteThemes(listOf(theme))
    }

    override suspend fun getThemesWithCharacterIncluded(characterId: Character.Id): List<Theme> {
        return themes.values.filter { it.containsCharacter(characterId) }.toList()
    }

    override suspend fun updateThemes(themes: List<Theme>) {
        this.themes.putAll(themes.map { it.id to it })
    }
}