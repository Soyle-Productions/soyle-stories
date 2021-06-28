package com.soyle.stories.layout.config.fixed

import com.soyle.stories.di.get
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.scene.sceneSetting.SceneSettingView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.tab
import kotlin.reflect.KClass

object SceneSetting : ToolConfig<SceneSetting>, FixedTool() {

    override fun toString(): String {
        return "Scene Setting"
    }

    override fun getFixedType(): FixedTool? = SceneSetting

    override fun getRegistration(): Pair<KClass<SceneSetting>, ToolConfig<SceneSetting>> {
        return SceneSetting::class to this
    }

    override fun getTabConfig(tool: ToolViewModel, type: SceneSetting): ToolTabConfig = object : ToolTabConfig {
        override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
            val view = projectScope.get<SceneSettingView>()
            view.title = getViewModelConfig(type).toolName()
            return tabPane.tab(view)
        }
    }

    override fun getViewModelConfig(type: SceneSetting): ToolViewModelConfig = object : ToolViewModelConfig {
        override fun toolName(): String = "Scene Setting"
    }

}