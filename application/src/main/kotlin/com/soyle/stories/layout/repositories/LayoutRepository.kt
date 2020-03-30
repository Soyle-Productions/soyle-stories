package com.soyle.stories.layout.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 12:36 PM
 */
interface LayoutRepository {

    suspend fun getLayoutForProject(projectId: Project.Id): Layout?
    fun getLayoutContainingTool(toolId: Tool.Id): Layout?
    fun getLayoutsContainingToolIds(toolIds: Set<Tool.Id>): List<Layout>
    suspend fun saveLayout(layout: Layout)

    //fun getToolsWithCharacterIdInIdentifyingData(characterId: Character.Id): List<Tool<*>>

}