package com.soyle.stories.project.layout

import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 10:02 PM
 */
interface LayoutViewListener {

    fun loadLayoutForProject(projectId: UUID)
    suspend fun toggleToolOpen(toolId: String)
    suspend fun closeTool(toolId: String)
    fun openDialog(dialog: Dialog)
    fun closeDialog(dialog: KClass<out Dialog>)

}