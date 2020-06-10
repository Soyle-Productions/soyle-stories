package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.entities.Scene
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.sceneDetails.SceneDetailsScope
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.find
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

object SceneDetailsConfig : ToolConfig<SceneDetails> {

	override fun getFixedType(): FixedTool? = null
	override fun getRegistration(): Pair<KClass<SceneDetails>, ToolConfig<SceneDetails>> {
		return SceneDetails::class to this
	}

	override fun getViewModelConfig(type: SceneDetails): ToolViewModelConfig {
		return object : ToolViewModelConfig {
			override fun toolName(): String = "Scene"
		}
	}

	override fun getTabConfig(toolId: String, type: SceneDetails): ToolTabConfig {
		return object : ToolTabConfig {
			override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
				val scope = SceneDetailsScope(projectScope, type)
				val view = find<com.soyle.stories.scene.sceneDetails.SceneDetails>(scope = scope)
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

data class SceneDetails(val sceneId: UUID, private val locale: Locale) : DynamicTool() {

	override suspend fun validate(context: OpenToolContext) {
		context.sceneRepository.getSceneById(Scene.Id(sceneId))
		  ?: throw SceneDoesNotExist(locale, sceneId)
	}

	override fun identifiedWithId(id: UUID): Boolean {
		return id == sceneId
	}
}