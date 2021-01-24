package com.soyle.stories.layout.config.fixed

import com.soyle.stories.di.get
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.tab
import kotlin.reflect.KClass

object SceneList : ToolConfig<SceneList>, FixedTool() {

	override fun getFixedType(): FixedTool? = SceneList

	override fun getRegistration(): Pair<KClass<SceneList>, ToolConfig<SceneList>> {
		return SceneList::class to this
	}

	override fun getTabConfig(tool: ToolViewModel, type: SceneList): ToolTabConfig = object : ToolTabConfig {
		override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
			val list = projectScope.get<com.soyle.stories.scene.sceneList.SceneList>()
			return tabPane.tab(list)
		}
	}

	override fun getViewModelConfig(type: SceneList): ToolViewModelConfig = object : ToolViewModelConfig {
		override fun toolName(): String = "Scenes"
	}

}