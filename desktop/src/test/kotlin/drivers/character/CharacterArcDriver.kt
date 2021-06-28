package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentController
import com.soyle.stories.characterarc.planNewCharacterArc.PlanNewCharacterArcController
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.usecase.character.CharacterArcRepository
import kotlinx.coroutines.runBlocking

class CharacterArcDriver private constructor(private val projectScope: ProjectScope) {

    private val timeMachine: MutableMap<CharacterArc.Id, MutableList<CharacterArc>> = mutableMapOf()
    private fun logNextVersion(arc: CharacterArc)
    {
        val previousVersions = timeMachine.getOrElse(arc.id) { mutableListOf() }
        if (previousVersions.lastOrNull() != arc) {
            timeMachine[arc.id] = previousVersions.apply { add(arc) }
        }
    }

    fun givenCharacterArcNamed(characterId: Character.Id, name: String): CharacterArc =
        getCharacterArcNamed(characterId, name) ?: createCharacterArcNamed(characterId, name).run {
            getCharacterArcNamed(characterId, name) ?: error("Character arc named $name was not created for $characterId")
        }

    fun getCharacterArcNamed(characterId: Character.Id, name: String): CharacterArc?
    {
        val repo = projectScope.get<CharacterArcRepository>()
        return runBlocking {
            repo.listCharacterArcsForCharacter(characterId).find { it.name == name }
        }
    }

    private fun createCharacterArcNamed(characterId: Character.Id, name: String)
    {
        projectScope.get<PlanNewCharacterArcController>().planCharacterArc(characterId.uuid.toString(), name)
    }

    fun getPreviousVersions(arc: CharacterArc): List<CharacterArc> = timeMachine[arc.id]?.toList() ?: listOf()

    fun getCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc>
    {
        val characterArcRepository = projectScope.get<CharacterArcRepository>()
        val arcs = runBlocking { characterArcRepository.listAllCharacterArcsInTheme(themeId) }
        arcs.forEach(::logNextVersion)
        return arcs
    }

    fun getCharacterArcForCharacterAndThemeOrError(characterId: Character.Id, themeId: Theme.Id): CharacterArc =
        getCharacterArcForCharacterAndTheme(characterId, themeId) ?: error("No character arc for $characterId and $themeId found")

    fun getCharacterArcForCharacterAndTheme(characterId: Character.Id, themeId: Theme.Id): CharacterArc? {
        val characterArcRepository = projectScope.get<CharacterArcRepository>()
        val arc = runBlocking { characterArcRepository.getCharacterArcByCharacterAndThemeId(characterId, themeId) }
        arc?.let(::logNextVersion)
        return arc
    }

    fun getCharacterArcForCharacterAndThemeNamedOrError(characterName: String, themeName: String): CharacterArc {
        return getCharacterArcForCharacterAndThemeNamed(characterName, themeName) ?: error("No character arc for $characterName and $themeName found")
    }
    fun getCharacterArcForCharacterAndThemeNamed(characterName: String, themeName: String): CharacterArc? {
        return getCharacterArcForCharacterAndTheme(
            CharacterDriver(projectScope.get()).getCharacterByNameOrError(characterName).id,
            ThemeDriver(projectScope.get()).getThemeByNameOrError(themeName).id
        )
    }

    fun givenArcHasArcSectionInMoralArgument(arc: CharacterArc, templateSection: CharacterArcTemplateSection): CharacterArc {
        if (arc.moralArgument().arcSections.none { it.template == templateSection }) {
            addArcSectionToMoralArgument(arc, templateSection)
        }
        return getCharacterArcForCharacterAndThemeOrError(arc.characterId, arc.themeId)
    }
    fun addArcSectionToMoralArgument(arc: CharacterArc, templateSection: CharacterArcTemplateSection)
    {
        projectScope.get<AddArcSectionToMoralArgumentController>()
            .addCharacterArcSectionToMoralArgument(
                arc.themeId.uuid.toString(),
                arc.characterId.uuid.toString(),
                templateSection.id.uuid.toString()
            )
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { CharacterArcDriver(this) } }
        }
        operator fun invoke(workbench: WorkBench): CharacterArcDriver = workbench.scope.get()
    }

}