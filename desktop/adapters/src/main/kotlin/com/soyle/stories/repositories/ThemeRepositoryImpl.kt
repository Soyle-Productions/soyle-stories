package com.soyle.stories.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ThemeRepositoryImpl : ThemeRepository {

	val themes = mutableMapOf<Theme.Id, Theme>()
	private val themesBySymbolId = mutableMapOf<Symbol.Id, Theme.Id>()

	override suspend fun addTheme(theme: Theme) {
		themes[theme.id] = theme
		theme.symbols.forEach {
			themesBySymbolId[it.id] = theme.id
		}
	}


	override suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme? {
		return themesBySymbolId[symbolId]?.let { themes[it] }
	}

	override suspend fun getThemesContainingSymbols(symbolIds: Set<Symbol.Id>): Map<Symbol.Id, Theme> {
		return symbolIds.mapNotNull {
			val theme = getThemeContainingSymbolWithId(it)
			if (theme != null) it to theme
			else null
		}.toMap()
	}

	override suspend fun getThemeById(id: Theme.Id): Theme? = themes[id]

	override suspend fun updateTheme(theme: Theme) {
		deleteTheme(theme)
		addTheme(theme)
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

	override suspend fun deleteTheme(theme: Theme) {
		val currentTheme = themes.getValue(theme.id)
		currentTheme.symbols.forEach {
			themesBySymbolId.remove(it.id)
		}
		themes.remove(theme.id)
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

	override suspend fun updateThemes(themes: List<Theme>) = themes.forEach { updateTheme(it) }

	override suspend fun getSymbolIdsThatDoNotExist(symbolIds: Set<Symbol.Id>): Set<Symbol.Id> {
		return symbolIds - themes.values.asSequence().flatMap { it.symbols.asSequence() }.map { it.id }.toSet()
	}

	override suspend fun getThemesById(themeIds: Set<Theme.Id>): Set<Theme> = themeIds.mapNotNull { themes[it] }.toSet()
}