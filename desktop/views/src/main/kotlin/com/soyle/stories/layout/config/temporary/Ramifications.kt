package com.soyle.stories.layout.config.temporary

import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.layout.tools.TemporaryTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsScope
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass


object DeleteSceneRamificationsConfig : ToolConfig<DeleteSceneRamifications> {
	override fun getRegistration(): Pair<KClass<DeleteSceneRamifications>, ToolConfig<DeleteSceneRamifications>> {
		return DeleteSceneRamifications::class to this
	}

	override fun getFixedType(): FixedTool? = null

	override fun getViewModelConfig(type: DeleteSceneRamifications): ToolViewModelConfig = object : ToolViewModelConfig {
		override fun toolName(): String {
			return "Delete Scene Ramifications"
		}
	}

	override fun getTabConfig(tool: ToolViewModel, type: DeleteSceneRamifications): ToolTabConfig {
		return object : ToolTabConfig {
			override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
				val scope = DeleteSceneRamificationsScope(tool.toolId, type, projectScope)
				val view = scope.get<com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamifications>()
				view.title = tool.name
				val tab = tabPane.tab(view)
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

data class DeleteSceneRamifications(val sceneId: UUID, private val locale: SceneLocale) : TemporaryTool() {
	override suspend fun validate(context: OpenToolContext) {
		context.sceneRepository.getSceneById(Scene.Id(sceneId))
		  ?: throw SceneDoesNotExist(locale, sceneId)
	}

	override fun identifiedWithId(id: UUID): Boolean =
	  id == sceneId
}