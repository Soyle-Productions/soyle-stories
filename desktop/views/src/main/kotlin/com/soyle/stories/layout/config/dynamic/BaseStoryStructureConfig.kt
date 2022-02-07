package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureScope
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
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

	override fun getTabConfig(tool: ToolViewModel, type: BaseStoryStructure): ToolTabConfig {
		return object : ToolTabConfig {
			override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
				val scope = BaseStoryStructureScope(projectScope, tool.toolId, type)
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

data class BaseStoryStructure(val characterId: Character.Id, val themeId: UUID) : DynamicTool() {
	override val isTemporary: Boolean
		get() = false

	override suspend fun validate(context: OpenToolContext) {
		context.characterRepository.getCharacterById(characterId)
		  ?: throw CharacterDoesNotExist(characterId)
		context.themeRepository.getThemeById(Theme.Id(themeId))
		  ?: throw ThemeDoesNotExist(themeId)
	}

	override fun identifiedWithId(id: UUID): Boolean =
	  id == characterId.uuid || id == themeId
}