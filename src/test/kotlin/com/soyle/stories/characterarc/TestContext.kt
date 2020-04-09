package com.soyle.stories.characterarc

import com.soyle.stories.characterarc.repositories.CharacterArcRepository
import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme

class TestContext(
  initialCharacters: List<Character> = emptyList(),
  initialThemes: List<Theme> = emptyList(),
  initialCharacterArcs: List<CharacterArc> = emptyList(),

  updateCharacterArc: (CharacterArc) -> Unit = {}
) {

	val characterRepository = object : CharacterRepository {
		val characters = initialCharacters.associateBy { it.id }.toMutableMap()
		override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]

		override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> = characters.values.filter {
			it.projectId == projectId.uuid
		}
	}

	val themeRepository: ThemeRepository = object : ThemeRepository {
		val themes = initialThemes.associateBy { it.id }.toMutableMap()
		override suspend fun addNewTheme(theme: Theme) {
			themes[theme.id] = theme
		}

		override suspend fun getThemeById(themeId: Theme.Id): Theme? = themes[themeId]
	}

	val characterArcRepository: CharacterArcRepository = object : CharacterArcRepository {
		val characterArcs = initialCharacterArcs.associateBy { it.characterId to it.themeId }.toMutableMap()
		override suspend fun addNewCharacterArc(characterArc: CharacterArc) {
			characterArcs[characterArc.characterId to characterArc.themeId] = characterArc
		}

		override suspend fun updateCharacterArc(characterArc: CharacterArc) {
			characterArcs[characterArc.characterId to characterArc.themeId] = characterArc
			updateCharacterArc.invoke(characterArc)
		}

		override suspend fun getCharacterArcByCharacterAndThemeId(characterId: Character.Id, themeId: Theme.Id): CharacterArc? = characterArcs[characterId to themeId]

		override suspend fun listAllCharacterArcsInProject(projectId: Project.Id): List<CharacterArc> = characterArcs.values.toList()
	}

}