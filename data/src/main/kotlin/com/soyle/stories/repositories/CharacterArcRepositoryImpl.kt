package com.soyle.stories.repositories

import com.soyle.stories.entities.*

class CharacterArcRepositoryImpl : com.soyle.stories.theme.repositories.CharacterArcRepository {
	val characterArcs = mutableMapOf<Pair<Character.Id, Theme.Id>, CharacterArc>()
	override suspend fun addNewCharacterArc(characterArc: CharacterArc) {
		characterArcs[characterArc.characterId to characterArc.themeId] = characterArc
	}

	override suspend fun getCharacterArcByCharacterAndThemeId(
	  characterId: Character.Id,
	  themeId: Theme.Id
	): CharacterArc? = characterArcs[characterId to themeId]

	override suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc> = characterArcs.values.filter { it.themeId == themeId }
	override suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {
		characterArcs.remove(characterId to themeId)
	}
}