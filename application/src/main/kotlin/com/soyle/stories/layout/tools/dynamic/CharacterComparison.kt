package com.soyle.stories.layout.tools.dynamic

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.theme.ThemeDoesNotExist
import java.util.*

class CharacterComparison(val themeId: UUID, val characterId: UUID?) : DynamicTool() {

	override val isTemporary: Boolean
		get() = false

	override suspend fun validate(context: OpenToolContext) {
		if (characterId != null) {
			context.characterRepository.getCharacterById(Character.Id(characterId))
			  ?: throw CharacterDoesNotExist(characterId)
		}
		context.themeRepository.getThemeById(Theme.Id(themeId))
		  ?: throw ThemeDoesNotExist(themeId)
	}

	override fun identifiedWithId(id: UUID): Boolean =
	  id == themeId

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as CharacterComparison

		if (themeId != other.themeId) return false

		return true
	}

	override fun hashCode(): Int {
		return themeId.hashCode()
	}

	override fun toString(): String {
		return "CharacterComparison($themeId, $characterId)"
	}


}