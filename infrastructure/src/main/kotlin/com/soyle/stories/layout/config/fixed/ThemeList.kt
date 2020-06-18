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

object ThemeList : ToolConfig<ThemeList>, FixedTool() {

    override fun getFixedType(): FixedTool? = ThemeList

    override fun getRegistration(): Pair<KClass<ThemeList>, ToolConfig<ThemeList>> {
        return ThemeList::class to this
    }

    override fun getTabConfig(tool: ToolViewModel, type: ThemeList): ToolTabConfig = object : ToolTabConfig {
        override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
            val list = projectScope.get<com.soyle.stories.theme.themeList.ThemeList>()
            list.title = getViewModelConfig(type).toolName()
            return tabPane.tab(list)
        }
    }

    override fun getViewModelConfig(type: ThemeList): ToolViewModelConfig = object : ToolViewModelConfig {
        override fun toolName(): String = "Themes"
    }

}