package com.soyle.stories.project.layout

import java.util.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 10:02 PM
 */
interface LayoutViewListener {

    suspend fun loadLayoutForProject(projectId: UUID)
    suspend fun toggleToolOpen(toolId: String)
    suspend fun closeTool(toolId: String)

}