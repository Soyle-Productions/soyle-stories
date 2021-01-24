package com.soyle.stories.layout.config

import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import tornadofx.Scope
import kotlin.reflect.KClass

interface ToolViewConfig {

    fun classesFor(toolType: ToolViewModel): ToolClassConfig

}

interface ToolClassConfig {
    fun createScope(scope: ProjectScope): ToolScope
    fun getViewClass(scope: ToolScope): KClass<*>
}

abstract class ToolScope : Scope()