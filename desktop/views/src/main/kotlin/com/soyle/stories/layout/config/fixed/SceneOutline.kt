package com.soyle.stories.layout.config.fixed

import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.scene.outline.SceneOutlineComponent
import com.soyle.stories.scene.sceneCharacters.SceneCharactersView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.add
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

object SceneOutline: ToolConfig<SceneOutline>, FixedTool() {

    override fun toString(): String {
        return "Scene Outline"
    }

    override fun getFixedType(): FixedTool = this

    override fun getRegistration(): Pair<KClass<SceneOutline>, ToolConfig<SceneOutline>> {
        return SceneOutline::class to this
    }

    override fun getTabConfig(tool: ToolViewModel, type: SceneOutline): ToolTabConfig = object : ToolTabConfig {
        override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
            val view = projectScope.get<SceneOutlineComponent>().SceneOutline()
            val tab = Tab(getViewModelConfig(type).toolName(), view)
            tabPane.tabs.add(tab)
            return tab
        }
    }

    override fun getViewModelConfig(type: SceneOutline): ToolViewModelConfig = object : ToolViewModelConfig {
        override fun toolName(): String = "Scene Outline"
    }

}