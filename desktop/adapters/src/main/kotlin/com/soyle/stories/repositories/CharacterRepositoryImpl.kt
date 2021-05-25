package com.soyle.stories.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.character.CharacterRepository

class CharacterRepositoryImpl : CharacterRepository {

	val characters = mutableMapOf<Character.Id, Character>()

	override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]

	override suspend fun getCharacters(characterIds: Set<Character.Id>): List<Character> {
		return characterIds.mapNotNull(characters::get)
	}

	override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> = characters.values.toList()
	override suspend fun addNewCharacter(character: Character) {
		characters[character.id] = character
	}

	override suspend fun deleteCharacterWithId(characterId: Character.Id) {
		characters.remove(characterId)
	}

	override suspend fun updateCharacter(character: Character) {
		characters[character.id] = character
	}

	override suspend fun getCharacterIdsThatDoNotExist(characterIdsToTest: Set<Character.Id>): Set<Character.Id> {
		return characterIdsToTest.asSequence().filterNot { it in characters }.toSet()
	}
}