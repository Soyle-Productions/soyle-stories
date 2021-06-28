package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.di.get
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.scene.sceneEditor.SceneEditorScope
import com.soyle.stories.scene.sceneEditor.SceneEditorView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

class SceneEditor(val sceneId: Scene.Id, val proseId: Prose.Id) : DynamicTool() {
    private var sceneName: String = ""

    override suspend fun validate(context: OpenToolContext) {
        sceneName = context.sceneRepository.getSceneOrError(sceneId.uuid).name.value
    }

    override fun identifiedWithId(id: UUID): Boolean = id == sceneId.uuid

    override fun toString(): String = "Scene Editor($sceneId, $sceneName)"
    override fun hashCode(): Int = sceneId.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return (other as? SceneEditor)?.sceneId == sceneId
    }

    companion object Config : ToolConfig<SceneEditor> {

        override fun getFixedType(): FixedTool? = null
        override fun getRegistration(): Pair<KClass<SceneEditor>, ToolConfig<SceneEditor>> {
            return SceneEditor::class to this
        }

        override fun getViewModelConfig(type: SceneEditor): ToolViewModelConfig = object : ToolViewModelConfig {
            override fun toolName(): String = "Scene Editor"
        }

        override fun getTabConfig(tool: ToolViewModel, type: SceneEditor): ToolTabConfig = object :
            ToolTabConfig {
            override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
                val scope = SceneEditorScope(projectScope, tool.toolId, type)
                val view = scope.get<SceneEditorView>()
                view.title = tool.name + " - " + type.sceneName
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