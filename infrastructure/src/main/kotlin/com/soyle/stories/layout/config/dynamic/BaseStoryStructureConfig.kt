package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureScope
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

object BaseStoryStructureConfig : ToolConfig<BaseStoryStructure> {

	override fun getRegistration(): Pair<KClass<BaseStoryStructure>, ToolConfig<BaseStoryStructure>> {
		return BaseStoryStructure::class to this
	}

	override fun getFixedType(): FixedTool? = null

	override fun getViewModelConfig(type: BaseStoryStructure): ToolViewModelConfig {
		return object : ToolViewModelConfig {
			override fun toolName(): String = "Base Story Structure: ${type.themeId}"
		}
	}

	override fun getTabConfig(toolId: String, type: BaseStoryStructure): ToolTabConfig {
		return object : ToolTabConfig {
			override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
				val scope = BaseStoryStructureScope(projectScope, toolId, type)
				val structure = find<com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructure>(scope = scope)
				val tab = tabPane.tab(structure)
				tab.tabPaneProperty().onChange {
					if (it == null) {
						scope.close()
					}
				}
				return tab
			}
		}
	}

}

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