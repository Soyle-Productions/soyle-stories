package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import java.util.*

interface CharacterRepository {
    suspend fun addNewCharacter(character: Character)
    suspend fun listCharactersInProject(projectId: Project.Id): List<Character>
    suspend fun getCharacterById(characterId: Character.Id): Character?
    suspend fun getCharacters(characterIds: Set<Character.Id>): List<Character>

    suspend fun getCharacterOrError(characterId: UUID) = getCharacterById(Character.Id(characterId))
        ?: throw CharacterDoesNotExist(characterId)
    suspend fun updateCharacter(character: Character)
    suspend fun deleteCharacterWithId(characterId: Character.Id)

    suspend fun getCharacterIdsThatDoNotExist(characterIdsToTest: Set<Character.Id>): Set<Character.Id>
}