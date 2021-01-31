package com.soyle.stories.layout.config.fixed

import com.soyle.stories.di.get
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.tab
import kotlin.reflect.KClass

object SceneSymbols : ToolConfig<SceneSymbols>, FixedTool() {

    override fun toString(): String {
        return "Symbols in Scene"
    }

    override fun getFixedType(): FixedTool? = SceneSymbols

    override fun getRegistration(): Pair<KClass<SceneSymbols>, ToolConfig<SceneSymbols>> {
        return SceneSymbols::class to this
    }

    override fun getTabConfig(tool: ToolViewModel, type: SceneSymbols): ToolTabConfig = object : ToolTabConfig {
        override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
            val view = projectScope.get<SymbolsInSceneView>()
            view.title = getViewModelConfig(type).toolName()
            return tabPane.tab(view)
        }
    }

    override fun getViewModelConfig(type: SceneSymbols): ToolViewModelConfig = object : ToolViewModelConfig {
        override fun toolName(): String = "Symbols in Scene"
    }

}