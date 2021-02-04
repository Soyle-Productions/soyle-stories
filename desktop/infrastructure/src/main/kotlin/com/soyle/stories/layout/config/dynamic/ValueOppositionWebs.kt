package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.sceneDetails.SceneDetailsScope
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.find
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

object ValueOppositionWebsConfig : ToolConfig<ValueOppositionWebs> {

	override fun getFixedType(): FixedTool? = null
	override fun getRegistration(): Pair<KClass<ValueOppositionWebs>, ToolConfig<ValueOppositionWebs>> {
		return ValueOppositionWebs::class to this
	}

	override fun getViewModelConfig(type: ValueOppositionWebs): ToolViewModelConfig {
		return object : ToolViewModelConfig {
			override fun toolName(): String = "Thematic Values"
		}
	}

	override fun getTabConfig(tool: ToolViewModel, type: ValueOppositionWebs): ToolTabConfig {
		return object : ToolTabConfig {
			override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
				val scope = ValueOppositionWebsScope(projectScope, tool.toolId, type)
				val view = find<com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs>(scope = scope)
				view.title = tool.name
				val tab = tabPane.tab(view)
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

data class ValueOppositionWebs(val themeId: UUID) : DynamicTool() {

	override suspend fun validate(context: OpenToolContext) {
		context.themeRepository.getThemeById(Theme.Id(themeId))
		  ?: throw ThemeDoesNotExist(themeId)
	}

	override fun identifiedWithId(id: UUID): Boolean {
		return id == themeId
	}
}