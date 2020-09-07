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
import com.soyle.stories.theme.characterConflict.CharacterConflictScope
import com.soyle.stories.theme.moralArgument.MoralArgumentScope
import com.soyle.stories.theme.moralArgument.MoralArgumentView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.UIComponent
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

class MoralArgument(val themeId: UUID) : DynamicTool() {

    override suspend fun validate(context: OpenToolContext) {
        context.themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
    }

    override fun identifiedWithId(id: UUID): Boolean = id == themeId

    override fun toString(): String = "Moral Argument($themeId)"
    override fun hashCode(): Int = themeId.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return (other as? MoralArgument)?.themeId == themeId
    }

    companion object Config : ToolConfig<MoralArgument> {

        override fun getFixedType(): FixedTool? = null
        override fun getRegistration(): Pair<KClass<MoralArgument>, ToolConfig<MoralArgument>> {
            return MoralArgument::class to this
        }

        override fun getViewModelConfig(type: MoralArgument): ToolViewModelConfig = object : ToolViewModelConfig {
            override fun toolName(): String = "Moral Argument"
        }

        override fun getTabConfig(tool: ToolViewModel, type: MoralArgument): ToolTabConfig = object :
            ToolTabConfig {
            override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
                val scope = MoralArgumentScope(projectScope, tool.toolId, type)
                val view = scope.get<MoralArgumentView>()
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