package com.soyle.stories.repositories

import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.ValueWeb
import java.util.*

class ThemeRepositoryImpl : ThemeRepository, com.soyle.stories.theme.repositories.ThemeRepository, com.soyle.stories.character.repositories.ThemeRepository {
	val themes = mutableMapOf<Theme.Id, Theme>()
	override suspend fun addNewTheme(theme: Theme) {
		themes[theme.id] = theme
	}

	override suspend fun addTheme(theme: Theme) = addNewTheme(theme)

	override suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme? {
		return themes.values.find { it.symbols.any { it.id == symbolId } }
	}

	override suspend fun getThemeById(themeId: Theme.Id): Theme? = themes[themeId]
	override suspend fun updateTheme(theme: Theme) {
		themes[theme.id] = theme
	}

	override suspend fun getThemeContainingOppositionsWithSymbolicEntityId(symbolicId: UUID): List<Theme> {
		return themes.values.filter {
			it.valueWebs.any {
				it.oppositions.any {
					it.representations.any {
						it.entityUUID == symbolicId
					}
				}
			}
		}
	}

	override suspend fun listThemesInProject(projectId: Project.Id): List<Theme> {
		return themes.values.filter { it.projectId == projectId }
	}

	override suspend fun deleteThemes(themes: List<Theme>) {
		themes.forEach {
			this.themes.remove(it.id)
		}
	}

	override suspend fun deleteTheme(theme: Theme) {
		deleteThemes(listOf(theme))
	}

	override suspend fun getThemesWithCharacterIncluded(characterId: Character.Id): List<Theme> {
		return themes.values.filter { it.containsCharacter(characterId) }.toList()
	}

	override suspend fun getThemeContainingValueWebWithId(valueWebId: ValueWeb.Id): Theme? {
		return themes.values.find {
			it.valueWebs.any { it.id == valueWebId }
		}
	}

	override suspend fun getThemeContainingOppositionValueWithId(oppositionValueId: OppositionValue.Id): Theme? {
		return themes.values.find {
			it.valueWebs.any {
				it.oppositions.any { it.id == oppositionValueId }
			}
		}
	}

	override suspend fun updateThemes(themes: List<Theme>) {
		this.themes.putAll(themes.map { it.id to it })
	}
}