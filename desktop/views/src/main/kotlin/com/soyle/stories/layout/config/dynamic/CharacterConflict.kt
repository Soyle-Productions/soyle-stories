package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.di.get
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.theme.characterConflict.CharacterConflictScope
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

class CharacterConflict(val themeId: UUID, val characterId: UUID? = null) : DynamicTool() {

    override suspend fun validate(context: OpenToolContext) {
        context.themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
    }

    override fun identifiedWithId(id: UUID): Boolean = id == themeId

    override fun toString(): String = "Character Conflict($themeId)"
    override fun hashCode(): Int = themeId.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return (other as? CharacterConflict)?.themeId == themeId
    }

    companion object Config : ToolConfig<CharacterConflict> {

        override fun getFixedType(): FixedTool? = null
        override fun getRegistration(): Pair<KClass<CharacterConflict>, ToolConfig<CharacterConflict>> {
            return CharacterConflict::class to this
        }

        override fun getViewModelConfig(type: CharacterConflict): ToolViewModelConfig = object : ToolViewModelConfig {
            override fun toolName(): String = "Character Conflict"
        }

        override fun getTabConfig(tool: ToolViewModel, type: CharacterConflict): ToolTabConfig = object : ToolTabConfig {
            override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
                val scope = CharacterConflictScope(projectScope, tool.toolId, type)
                val view = scope.get<com.soyle.stories.theme.characterConflict.CharacterConflict>()
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