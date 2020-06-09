package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.storyevent.StoryEventDoesNotExist
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsScope
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.find
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

object StoryEventDetailsConfig : ToolConfig<StoryEventDetails> {
	override fun getRegistration(): Pair<KClass<StoryEventDetails>, ToolConfig<StoryEventDetails>> {
		return StoryEventDetails::class to this
	}

	override fun getFixedType(): FixedTool? = null
	override fun getViewModelConfig(type: StoryEventDetails): ToolViewModelConfig {
		return object : ToolViewModelConfig {
			override fun toolName(): String = "Story Event"
		}
	}

	override fun getTabConfig(toolId: String, type: StoryEventDetails): ToolTabConfig {
		return object : ToolTabConfig {
			override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
				val scope = StoryEventDetailsScope(projectScope, type)
				val structure = find<com.soyle.stories.storyevent.storyEventDetails.StoryEventDetails>(scope = scope)
				val tab = tabPane.tab(structure)
				tab.tabPaneProperty().onChange {
					if (it == null) {
						scope.close()
					}
				}
				return tab
			}
		}
	}
}

data class StoryEventDetails(val storyEventId: UUID) : DynamicTool() {

	override val isTemporary: Boolean
		get() = false

	override suspend fun validate(context: OpenToolContext) {
		context.storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		  ?: throw StoryEventDoesNotExist(storyEventId)
	}

	override fun identifiedWithId(id: UUID): Boolean =
	  id == storyEventId
}