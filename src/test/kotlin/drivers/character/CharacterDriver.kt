package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryController
import com.soyle.stories.character.renameCharacter.RenameCharacterController
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import kotlinx.coroutines.runBlocking

class CharacterDriver private constructor(private val projectScope: ProjectScope)
{

    private val characterNameHistory = mutableMapOf<String, MutableSet<Character.Id>>().withDefault { mutableSetOf() }

    fun givenCharacterNamed(characterName: NonBlankString): Character =
        getCharacterByName(characterName.value) ?: createCharacterWithName(characterName)

    fun getCharacterByNameOrError(characterName: String): Character =
        getCharacterByName(characterName) ?: throw NoSuchElementException("No character named $characterName in project ${projectScope.projectViewModel.name}")

    fun getCharacterByName(characterName: String): Character? {
        val characterRepository = projectScope.get<CharacterRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allCharacters = runBlocking { characterRepository.listCharactersInProject(projectId) }
        allCharacters.onEach { characterNameHistory[it.name.value] = characterNameHistory.getValue(it.name.value).apply { add(it.id) } }
        return allCharacters.find { it.name.value == characterName }
    }

    fun getCharacterAtOnePointNamed(formerName: String): Character?
    {
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

    fun givenCharacterRenamedTo(characterId: Character.Id, name: String)
    {
        projectScope.get<RenameCharacterController>()
            .renameCharacter(characterId.uuid.toString(), NonBlankString.create(name)!!)
    }

    fun givenCharacterRemoved(character: Character) {
        projectScope.get<RemoveCharacterFromStoryController>()
            .removeCharacter(character.id.uuid.toString())
    }

    companion object {
        private var isFirstCall = true
        operator fun invoke(workBench: WorkBench): CharacterDriver
        {
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