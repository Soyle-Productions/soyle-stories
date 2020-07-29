package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.di.get
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparisonScope
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

class CharacterValueComparison(val themeId: UUID) : DynamicTool() {

    override suspend fun validate(context: OpenToolContext) {
        context.themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
    }

    override fun identifiedWithId(id: UUID): Boolean = id == themeId

    override fun toString(): String = "Character Value Comparison($themeId)"
    override fun hashCode(): Int = themeId.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return (other as? CharacterValueComparison)?.themeId == themeId
    }

    companion object Config : ToolConfig<CharacterValueComparison> {

        override fun getFixedType(): FixedTool? = null
        override fun getRegistration(): Pair<KClass<CharacterValueComparison>, ToolConfig<CharacterValueComparison>> {
            return CharacterValueComparison::class to this
        }

        override fun getViewModelConfig(type: CharacterValueComparison): ToolViewModelConfig = object : ToolViewModelConfig {
            override fun toolName(): String = "Character Value Comparison"
        }

        override fun getTabConfig(tool: ToolViewModel, type: CharacterValueComparison): ToolTabConfig = object : ToolTabConfig {
            override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
                val scope = CharacterValueComparisonScope(projectScope, tool.toolId, type)
                val view = scope.get<com.soyle.stories.theme.characterValueComparison.CharacterValueComparison>()
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