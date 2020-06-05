package com.soyle.stories.layout.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 12:23 PM
 */
class MockLayoutRepository(
    getLayoutForProject: Layout? = null,
    getLayoutContainingTool: Layout? = null,
    getToolsWithCharacterIdInIdentifyingData: List<Tool>? = null,
    getLayoutsContainingToolIds: List<Layout>? = null
) : LayoutRepository {

    private val getLayoutForProjectReturn = getLayoutForProject
    private val getLayoutContainingToolReturn = getLayoutContainingTool
    private val getToolsWithCharacterIdInIdentifyingDataReturn = getToolsWithCharacterIdInIdentifyingData
    private val getLayoutsContainingToolIdsReturn = getLayoutsContainingToolIds

    private val calls = mutableSetOf<Function<*>>()

    fun wasCalled(function: Function<*>) = calls.contains(function)

    override suspend fun getLayoutForProject(projectId: Project.Id): Layout? {
        calls.add(::getLayoutForProject)
        return getLayoutForProjectReturn
    }

    override fun getLayoutsContainingToolIds(toolIds: Set<Tool.Id>): List<Layout> {
        calls.add(::getLayoutsContainingToolIds)
        return getLayoutsContainingToolIdsReturn ?: emptyList()
    }

    override suspend fun saveLayout(layout: Layout) {
        calls.add(::saveLayout)
    }

    override fun getLayoutContainingTool(toolId: Tool.Id): Layout? {
        calls.add(::getLayoutContainingTool)
        return getLayoutContainingToolReturn
    }

}