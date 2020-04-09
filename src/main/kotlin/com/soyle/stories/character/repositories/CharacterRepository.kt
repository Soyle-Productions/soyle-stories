package com.soyle.stories.character.repositories

import com.soyle.stories.entities.Character

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 2:51 PM
 */
interface CharacterRepository {
    suspend fun addNewCharacter(character: Character)
    suspend fun getCharacterById(characterId: Character.Id): Character?
    suspend fun updateCharacter(character: Character)
    suspend fun deleteCharacterWithId(characterId: Character.Id)
}