package com.soyle.stories.layout.config

import com.soyle.stories.project.ProjectScope
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

interface ToolTabConfig {

	fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab

}