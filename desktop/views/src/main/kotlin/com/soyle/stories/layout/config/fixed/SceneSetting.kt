package com.soyle.stories.layout.config.fixed

import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.scene.sceneList.SceneListModel
import com.soyle.stories.scene.sceneList.SceneListView
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.scene.setting.SceneSettingToolLocale
import com.soyle.stories.scene.setting.SceneSettingToolRoot
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Pane
import tornadofx.add
import tornadofx.tab
import java.util.*
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
            val factory = projectScope.get<SceneSettingToolRoot.Factory>()
            val locale = projectScope.get<SceneSettingToolLocale>()
            val tab = Tab().apply {
                val selectedScene = projectScope.get<SceneListModel>().selectedItem.value?.let {
                    it.id to it.name
                }
                val sceneSettingRoot = factory.invoke(selectedScene)
                content = sceneSettingRoot
                textProperty().bind(locale.sceneSettingToolTitle)
            }
            tabPane.tabs.add(tab)
            return tab
        }
    }

    override fun getViewModelConfig(type: SceneSetting): ToolViewModelConfig = object : ToolViewModelConfig {
        override fun toolName(): String = "Scene Setting"
    }

}