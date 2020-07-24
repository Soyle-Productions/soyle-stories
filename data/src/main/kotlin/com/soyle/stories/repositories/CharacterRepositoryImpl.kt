package com.soyle.stories.repositories

import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project

class CharacterRepositoryImpl : CharacterRepository, com.soyle.stories.character.repositories.CharacterRepository, com.soyle.stories.theme.repositories.CharacterRepository {

	val characters = mutableMapOf<Character.Id, Character>()

	override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]

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
}