package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project

/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 2:51 PM
 */
interface CharacterRepository {
    suspend fun getCharacterById(characterId: Character.Id): Character?
    suspend fun listCharactersInProject(projectId: Project.Id): List<Character>
}