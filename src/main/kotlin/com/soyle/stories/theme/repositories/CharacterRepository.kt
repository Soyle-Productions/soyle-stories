package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.Character

/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 2:51 PM
 */
interface CharacterRepository {
    suspend fun getCharacterById(characterId: Character.Id): Character?
}