package com.soyle.stories.theme.doubles

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.theme.repositories.CharacterRepository

class CharacterRepositoryDouble : CharacterRepository {

    val characters = mutableMapOf<Character.Id, Character>()

    override suspend fun getCharacterById(characterId: Character.Id): Character? {
        return characters[characterId]
    }

    override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> {
        return characters.values.filter { it.projectId == projectId }
    }

}