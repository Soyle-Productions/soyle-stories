package com.soyle.stories.layout.tools.dynamic

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.theme.ThemeDoesNotExist
import java.util.*

data class BaseStoryStructure(val characterId: UUID, val themeId: UUID) : DynamicTool() {
	override val isTemporary: Boolean
		get() = false

	override suspend fun validate(context: OpenToolContext) {
		context.characterRepository.getCharacterById(Character.Id(characterId))
		  ?: throw CharacterDoesNotExist(characterId)
		context.themeRepository.getThemeById(Theme.Id(themeId))
		  ?: throw ThemeDoesNotExist(themeId)
	}

	override fun identifiedWithId(id: UUID): Boolean =
		id == characterId || id == themeId
}