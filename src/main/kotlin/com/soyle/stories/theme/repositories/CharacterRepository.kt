package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project

interface CharacterRepository {
    suspend fun getCharacterById(characterId: Character.Id): Character?
    suspend fun listCharactersInProject(projectId: Project.Id): List<Character>
    suspend fun updateCharacter(character: Character)
}