package com.soyle.stories.layout.config.fixed

import com.soyle.stories.di.get
import com.soyle.stories.domain.project.Project
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.storyevent.list.StoryEventListTool
import com.soyle.stories.storyevent.timeline.TimelineView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.add
import kotlin.reflect.KClass

object Timeline : ToolConfig<Timeline>, FixedTool() {

    override fun getFixedType(): FixedTool = Timeline

    override fun getRegistration(): Pair<KClass<Timeline>, ToolConfig<Timeline>> {
        return Timeline::class to this
    }

    override fun getTabConfig(tool: ToolViewModel, type: Timeline): ToolTabConfig = object : ToolTabConfig {
        override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
            val timeline = projectScope.get<TimelineView>()
            timeline.title = getViewModelConfig(type).toolName()
            tabPane.add(timeline.root)
            return tabPane.tabs.last()
        }
    }

    override fun getViewModelConfig(type: Timeline): ToolViewModelConfig = object : ToolViewModelConfig {
        override fun toolName(): String = "Timeline"
    }

}