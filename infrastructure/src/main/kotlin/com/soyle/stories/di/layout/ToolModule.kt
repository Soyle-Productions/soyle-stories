package com.soyle.stories.di.layout

import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.config.dynamic.*
import com.soyle.stories.layout.config.fixed.CharacterList
import com.soyle.stories.layout.config.fixed.LocationList
import com.soyle.stories.layout.config.fixed.SceneList
import com.soyle.stories.layout.config.fixed.ThemeList
import com.soyle.stories.layout.config.temporary.DeleteSceneRamificationsConfig
import com.soyle.stories.layout.config.temporary.ReorderSceneRamificationsConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.layout.tools.ToolType
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.RegisteredToolsConfig
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import kotlin.reflect.KClass

object ToolModule : RegisteredToolsConfig {

    private val registeredTools = mapOf<KClass<out ToolType>, ToolConfig<*>>(
        /**
         * Fixed Tools
         */
        CharacterList.getRegistration(),
        SceneList.getRegistration(),
        LocationList.getRegistration(),
        ThemeList.getRegistration(),
        /**
         * Dynamic Tools
         */
        BaseStoryStructureConfig.getRegistration(),
        LocationDetailsConfig.getRegistration(),
        SceneDetailsConfig.getRegistration(),
        ValueOppositionWebsConfig.getRegistration(),
        CharacterValueComparison.getRegistration(),
        CharacterConflict.getRegistration(),
        MoralArgument.getRegistration(),
        SceneEditor.getRegistration(),
        /**
         * Temporary Tools
         */
        DeleteSceneRamificationsConfig.getRegistration(),
        ReorderSceneRamificationsConfig.getRegistration()
    )

    override fun getConfigFor(type: ToolType): ToolViewModelConfig {
        return registeredTools
            .getOrElse(type::class) { error("$type has not been registered as a tool.") }
            .let { getViewModelConfig(it, type) }
    }

    private fun <T : ToolType> getViewModelConfig(config: ToolConfig<T>, type: ToolType): ToolViewModelConfig {
        val expectedType = config.getRegistration().first
        return if (expectedType.isInstance(type)) {
            config.getViewModelConfig(type as T)
        } else error("")
    }

    fun getTabConfigFor(tool: ToolViewModel): ToolTabConfig {
        return registeredTools
            .getValue(tool.type::class)
            .let { getTabConfig(it, tool, tool.type) }
    }

    private fun <T : ToolType> getTabConfig(config: ToolConfig<T>, tool: ToolViewModel, type: ToolType): ToolTabConfig {
        val expectedType = config.getRegistration().first
        return if (expectedType.isInstance(type)) {
            config.getTabConfig(tool, type as T)
        } else error("")
    }

    override fun listFixedToolTypes(): List<FixedTool> {
        return registeredTools.values.mapNotNull {
            it.getFixedType()
        }
    }

}