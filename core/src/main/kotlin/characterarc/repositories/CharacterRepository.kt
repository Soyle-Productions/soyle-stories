package com.soyle.stories.characterarc.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project

/**
 * Created by Brendan
 * Date: 2/25/2020
 * Time: 5:44 PM
 */
interface CharacterRepository {
    suspend fun listCharactersInProject(projectId: Project.Id): List<Character>
    suspend fun getCharacterById(characterId: Character.Id): Character?
}