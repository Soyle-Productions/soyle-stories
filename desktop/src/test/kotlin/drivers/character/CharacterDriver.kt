package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryController
import com.soyle.stories.character.renameCharacter.RenameCharacterController
import com.soyle.stories.characterarc.planNewCharacterArc.PlanNewCharacterArcController
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterRepository
import kotlinx.coroutines.runBlocking

class CharacterDriver private constructor(private val projectScope: ProjectScope) {

    private val characterNameHistory = mutableMapOf<String, MutableSet<Character.Id>>().withDefault { mutableSetOf() }

    fun givenCharacterNamed(characterName: NonBlankString): Character =
        getCharacterByName(characterName.value) ?: createCharacterWithName(characterName)

    fun getCharacterByNameOrError(characterName: String): Character =
        getCharacterByName(characterName)
            ?: throw NoSuchElementException("No character named $characterName in project ${projectScope.projectViewModel.name}")

    fun getCharacterByName(characterName: String): Character? {
        val characterRepository = projectScope.get<CharacterRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allCharacters = runBlocking { characterRepository.listCharactersInProject(projectId) }
        allCharacters.onEach {
            characterNameHistory[it.name.value] = characterNameHistory.getValue(it.name.value).apply { add(it.id) }
        }
        return allCharacters.find { it.name.value == characterName }
    }

    fun getCharacterAtOnePointNamed(formerName: String): Character? {
        val characterRepository = projectScope.get<CharacterRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val charactersById = runBlocking { characterRepository.listCharactersInProject(projectId) }
            .associateBy { it.id }
        return characterNameHistory.getValue(formerName).lastOrNull()?.let { charactersById[it] }
    }

    fun createCharacterWithName(characterName: NonBlankString): Character {
        projectScope.get<BuildNewCharacterController>()
            .createCharacter(characterName) { throw it }
        return getCharacterByNameOrError(characterName.value)
    }

    fun givenCharacterHasAnArcNamed(character: Character, name: String): CharacterArc =
        getCharacterArcByName(character, name) ?: createCharacterArcWithName(
            character,
            name
        ).run { getCharacterArcByNameOrError(character, name) }

    fun getCharacterArcByNameOrError(character: Character, name: String): CharacterArc =
        getCharacterArcByName(character, name) ?: error("No character arc for ${character.name} with name $name")

    fun getCharacterArcByName(character: Character, name: String): CharacterArc? {
        val repository = projectScope.get<CharacterArcRepository>()
        return runBlocking {
            repository.listCharacterArcsForCharacter(character.id)
                .find { it.name == name }
        }
    }

    fun getCharacterArcsForCharacter(character: Character): List<CharacterArc>
    {
        val repository = projectScope.get<CharacterArcRepository>()
        return runBlocking {
            repository.listCharacterArcsForCharacter(character.id)
        }
    }

    fun getCharacterArcByCharacterAndTheme(character: Character, theme: Theme): CharacterArc?
    {
        val repository = projectScope.get<CharacterArcRepository>()
        return runBlocking {
            repository.getCharacterArcByCharacterAndThemeId(character.id, theme.id)
        }
    }

    fun getCharacterArcSectionByNameOrError(character: Character, theme: Theme, sectionName: String): CharacterArcSection =
        getCharacterArcSectionByName(character, theme, sectionName) ?: error("${character.name} does not have a $sectionName section for the ${theme.name} theme")

    fun getCharacterArcSectionByName(character: Character, theme: Theme, sectionName: String): CharacterArcSection?
    {
        val arc = getCharacterArcByCharacterAndTheme(character, theme) ?: return null
        return arc.moralArgument().arcSections.find { it.template.name == sectionName }
    }

    private fun createCharacterArcWithName(character: Character, name: String) {
        projectScope.get<PlanNewCharacterArcController>()
            .planCharacterArc(character.id.uuid.toString(), name)
    }

    fun givenCharacterRenamedTo(characterId: Character.Id, name: String) {
        projectScope.get<RenameCharacterController>()
            .renameCharacter(characterId.uuid.toString(), NonBlankString.create(name)!!)
    }

    fun givenCharacterRemoved(character: Character) {
        projectScope.get<RemoveCharacterFromStoryController>()
            .confirmRemoveCharacter(character.id)
    }

    companion object {
        private var isFirstCall = true
        operator fun invoke(workBench: WorkBench): CharacterDriver {
            if (isFirstCall) {
                scoped<ProjectScope> {
                    provide { CharacterDriver(this) }
                }
                isFirstCall = false
            }
            return workBench.scope.get()
        }
    }

}