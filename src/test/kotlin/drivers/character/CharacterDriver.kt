package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
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

    fun givenCharacterNamed(characterName: NonBlankString): Character =
        getCharacterByName(characterName.value) ?: createCharacterWithName(characterName)

    fun getCharacterByNameOrError(characterName: String): Character =
        getCharacterByName(characterName) ?: throw NoSuchElementException("No character named $characterName in project ${projectScope.projectViewModel.name}")

    fun getCharacterByName(characterName: String): Character? {
        val characterRepository = projectScope.get<CharacterRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allCharacters = runBlocking { characterRepository.listCharactersInProject(projectId) }
        return allCharacters.find { it.name.value == characterName }
    }

    fun createCharacterWithName(characterName: NonBlankString): Character {
        projectScope.get<BuildNewCharacterController>()
            .createCharacter(characterName) { throw it }
        return getCharacterByNameOrError(characterName.value)
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