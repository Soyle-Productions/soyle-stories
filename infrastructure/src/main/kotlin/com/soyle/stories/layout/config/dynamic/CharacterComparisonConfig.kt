package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonScope
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.theme.ThemeDoesNotExist
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.find
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

object CharacterComparisonConfig : ToolConfig<CharacterComparison> {

	override fun getRegistration(): Pair<KClass<CharacterComparison>, ToolConfig<CharacterComparison>> {
		return CharacterComparison::class to this
	}

	override fun getFixedType(): FixedTool? = null

	override fun getViewModelConfig(type: CharacterComparison): ToolViewModelConfig = object : ToolViewModelConfig {
		override fun toolName(): String = "Character Comparison"
	}

	override fun getTabConfig(toolId: String, type: CharacterComparison): ToolTabConfig = object : ToolTabConfig {
		override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
			val scope = CharacterComparisonScope(projectScope, type.themeId.toString(), type.characterId.toString())
			val comparison = find<com.soyle.stories.characterarc.characterComparison.CharacterComparison>(scope = scope)
			val tab = tabPane.tab(comparison)
			tab.tabPaneProperty().onChange {
				if (it == null) {
					scope.close()
				}
			}
			return tab
		}
	}

}

class CharacterComparison(val themeId: UUID, val characterId: UUID?) : DynamicTool() {

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