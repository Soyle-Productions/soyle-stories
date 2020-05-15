package com.soyle.stories.character.usecases.removeCharacterFromLocalStory

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.RemoveCharacterFromLocalStoryFailure
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory.OutputPort
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory.ResponseModel
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.Context
import com.soyle.stories.layout.LayoutDoesNotExist
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*

class RemoveCharacterFromLocalStoryUseCase(
    projectId: UUID,
    private val context: Context,
    private val removeCharacterFromStory: RemoveCharacterFromStory
) : RemoveCharacterFromLocalStory {

    private val projectId = Project.Id(projectId)

    override suspend fun invoke(characterId: UUID, outputPort: OutputPort) {
        val removeCharacterResponse = try {
            attemptToRemoveCharacterFromStory(characterId)
        } catch (c: CharacterException) {
            return outputPort.receiveRemoveCharacterFromLocalStoryFailure(c)
        }
        val removedTools = removeToolsWithRemovedCharacterAndThemes(Character.Id(characterId), removeCharacterResponse.removedThemeIds)

        outputPort.receiveRemoveCharacterFromLocalStoryResponse(respond(characterId, removedTools, removeCharacterResponse))
    }

    private suspend fun attemptToRemoveCharacterFromStory(characterId: UUID): RemoveCharacterFromStory.ResponseModel {
        return try {
            removeCharacterFromStorySynchronously(characterId)
        } catch (c: CharacterException) {
            throw c
        } catch (e: Exception) {
            throw RemoveCharacterFromLocalStoryFailure(e)
        }
    }

    private suspend fun removeCharacterFromStorySynchronously(characterId: UUID): RemoveCharacterFromStory.ResponseModel {
        return suspendCancellableCoroutine {
            runBlocking(it.context) {
                removeCharacterFromStory.invoke(characterId, RemoveCharacterFromStoryOutputContinuation(it))
            }
        }
    }

    private suspend fun removeToolsWithRemovedCharacterAndThemes(characterId: Character.Id, themeIds: List<UUID>): List<Tool<*>> {
        val layout = getLayout()
        val tools = getToolsWithCharacterId(layout, characterId) + getToolsWithThemeIds(layout, themeIds)
        val nextLayout = getLayoutWithoutTools(tools, layout)
        context.layoutRepository.saveLayout(nextLayout)
        return tools
    }

    private suspend fun getLayout(): Layout
    {
        return context.layoutRepository.getLayoutForProject(projectId)
            ?: throw LayoutDoesNotExist()
    }

    private fun getToolsWithCharacterId(layout: Layout, characterId: Character.Id): List<Tool<*>>
    {
        return layout.tools.filter {
            it.identifiedWithCharacter(characterId)
        }
    }

    private fun getToolsWithThemeIds(layout: Layout, themeIds: List<UUID>): List<Tool<*>>
    {
        val idSet = themeIds.map(Theme::Id).toSet()
        return layout.tools.filter {
            it.identifiedWithAnyThemeIdIn(idSet)
        }
    }

    private fun getLayoutWithoutTools(tools: List<Tool<*>>, layout: Layout): Layout
    {
        return tools.fold(layout) { nextLayout, tool ->
            nextLayout.toolRemoved(tool.id)
        }
    }

    private fun respond(characterId: UUID, tools: List<Tool<*>>, removalResponse: RemoveCharacterFromStory.ResponseModel): ResponseModel = ResponseModel(
        characterId,
        removalResponse.removedThemeIds,
        removalResponse.affectedThemeIds,
        tools.map { it.id.uuid }
    )
}