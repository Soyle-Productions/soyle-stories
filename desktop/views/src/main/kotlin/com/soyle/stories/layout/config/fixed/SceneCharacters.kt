package com.soyle.stories.layout.config.fixed

import com.soyle.stories.di.get
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.scene.characters.tool.SceneCharactersToolComponent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.tab
import kotlin.reflect.KClass

object SceneCharacters : ToolConfig<SceneCharacters>, FixedTool() {

    override fun toString(): String {
        return "Scene Characters"
    }

    override fun getFixedType(): FixedTool? = SceneCharacters

    override fun getRegistration(): Pair<KClass<SceneCharacters>, ToolConfig<SceneCharacters>> {
        return SceneCharacters::class to this
    }

    override fun getTabConfig(tool: ToolViewModel, type: SceneCharacters): ToolTabConfig = object : ToolTabConfig {
        override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
            val view = projectScope.get<SceneCharactersToolComponent>()
            view.title = getViewModelConfig(type).toolName()
            return tabPane.tab(view)
        }
    }

    override fun getViewModelConfig(type: SceneCharacters): ToolViewModelConfig = object : ToolViewModelConfig {
        override fun toolName(): String = "Scene Characters"
    }

}