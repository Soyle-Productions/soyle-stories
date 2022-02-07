package com.soyle.stories.layout.config.fixed

import com.soyle.stories.di.get
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.ramifications.RamificationsView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.tab
import kotlin.reflect.KClass

object Ramifications : FixedTool(), ToolConfig<Ramifications> {

    override fun getFixedType(): FixedTool = this

    override fun getRegistration(): Pair<KClass<Ramifications>, ToolConfig<Ramifications>> =
        Ramifications::class to this

    override fun getTabConfig(tool: ToolViewModel, type: Ramifications): ToolTabConfig = object : ToolTabConfig {
        override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
            val view = projectScope.get<RamificationsView>()
            view.viewModel.toolId = tool.toolId
            view.title = getViewModelConfig(type).toolName()
            return tabPane.tab(view)
        }
    }

    override fun getViewModelConfig(type: Ramifications): ToolViewModelConfig = object : ToolViewModelConfig {
        override fun toolName(): String = "Ramifications"
    }

}