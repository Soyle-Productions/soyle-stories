package com.soyle.stories.theme.usecases.removeCharacterFromComparison

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.Context
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.theme.LocalThemeException
import com.soyle.stories.theme.RemoveCharacterFromComparisonFailure
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison.OutputPort
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison.ResponseModel
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.coroutines.suspendCoroutine

class RemoveCharacterFromLocalComparisonUseCase(
    projectId: UUID,
    private val removeCharacterFromComparison: RemoveCharacterFromComparison,
    private val context: Context
) : RemoveCharacterFromLocalComparison {

    private val projectId = Project.Id(projectId)

    override suspend fun invoke(themeId: UUID, characterId: UUID, output: OutputPort) {
        val response = try {
            removeCharacterFromLocalComparison(themeId, characterId)
        } catch (t: LocalThemeException) {
            return output.receiveRemoveCharacterFromLocalComparisonFailure(t)
        }
        output.receiveRemoveCharacterFromLocalComparisonResponse(response        )
    }

    private suspend fun removeCharacterFromLocalComparison(
        themeId: UUID,
        characterId: UUID
    ): ResponseModel {
        val removeCharacterFromComparisonResponse = tryToRemoveCharacterFromComparison(themeId, characterId)
        val removedTools =
            getRemovedToolsRelatedToThemeIfThemeDeleted(themeId, removeCharacterFromComparisonResponse.themeDeleted)
        return ResponseModel(
            themeId,
            characterId,
            removeCharacterFromComparisonResponse.themeDeleted,
            removedTools.map { it.id.uuid }
        )
    }

    private suspend fun tryToRemoveCharacterFromComparison(themeId: UUID, characterId: UUID): RemoveCharacterFromComparison.ResponseModel
    {
         return try {
            removeCharacterFromComparison(themeId, characterId)
        } catch (t: ThemeException) {
            throw RemoveCharacterFromComparisonFailure(t)
        }
    }

    private suspend fun getRemovedToolsRelatedToThemeIfThemeDeleted(themeId: UUID, themeDeleted: Boolean): List<Tool<*>> {
        return if (themeDeleted) {
            getRemovedToolsRelatedToTheme(themeId)
        } else emptyList()
    }

    private suspend fun getRemovedToolsRelatedToTheme(themeId: UUID): List<Tool<*>>
    {
        val layout = context.layoutRepository.getLayoutForProject(projectId)
        return if (layout != null) {
            getRemovedToolsFromLayoutRelatedToTheme(layout, themeId)
        } else emptyList()
    }

    private suspend fun getRemovedToolsFromLayoutRelatedToTheme(
        layout: Layout,
        themeId: UUID
    ): List<Tool<*>> {
        val toolsToRemove = getToolsRelatedToThemeInLayout(themeId, layout)
        removeToolsFromLayout(toolsToRemove, layout)
        return toolsToRemove
    }

    private suspend fun removeToolsFromLayout(
        toolsToRemove: List<Tool<*>>,
        layout: Layout
    ) {
        val nextLayout = toolsToRemove.fold(layout) { nextLayout, tool ->
            nextLayout.toolRemoved(tool.id)
        }
        context.layoutRepository.saveLayout(nextLayout)
    }

    private fun getToolsRelatedToThemeInLayout(
        themeId: UUID,
        layout: Layout
    ): List<Tool<*>> {
        return layout.tools.filter {
            it.identifiedWithAnyThemeIdIn(setOf(Theme.Id(themeId)))
        }
    }

    private suspend fun removeCharacterFromComparison(themeId: UUID, characterId: UUID): RemoveCharacterFromComparison.ResponseModel {
        return suspendCoroutine {
            runBlocking {
                removeCharacterFromComparison.invoke(themeId, characterId, RemoveCharacterFromComparisonOutputContinuation(it))
            }
        }
    }
}