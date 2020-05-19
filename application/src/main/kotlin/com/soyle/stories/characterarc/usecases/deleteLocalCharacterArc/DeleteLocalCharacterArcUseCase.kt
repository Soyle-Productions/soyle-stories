package com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc

import com.soyle.stories.characterarc.FailedToDemoteCharacter
import com.soyle.stories.characterarc.LocalCharacterArcException
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeleteCharacterArc
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc.OutputPort
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc.ResponseModel
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.Context
import com.soyle.stories.layout.LayoutDoesNotExist
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.coroutines.suspendCoroutine

class DeleteLocalCharacterArcUseCase(
    projectId: UUID,
    private val deleteCharacterArc: DeleteCharacterArc,
    private val context: Context
) : DeleteLocalCharacterArc {

    private val projectId = Project.Id(projectId)

    override suspend fun invoke(themeId: UUID, characterId: UUID, output: OutputPort) {
        val response = try {
            deleteLocalCharacterArc(themeId, characterId)
        } catch (l: LocalCharacterArcException) {
            return output.receiveDeleteLocalCharacterArcFailure(l)
        }
        output.receiveDeleteLocalCharacterArcResponse(response)
    }

    private suspend fun deleteLocalCharacterArc(
        themeId: UUID,
        characterId: UUID
    ): ResponseModel {
        val demoteCharacterResponse = tryToDeleteCharacterArc(themeId, characterId)
        val tools = removeAppropriateToolsFromLayout(themeId, characterId, demoteCharacterResponse.themeRemoved)
        return ResponseModel(
            themeId,
            characterId,
            demoteCharacterResponse.removedCharacterArcSections,
            tools.map { it.id.uuid },
            demoteCharacterResponse.themeRemoved
        )
    }

    private suspend fun removeAppropriateToolsFromLayout(
        themeId: UUID,
        characterId: UUID,
        removeToolsForTheme: Boolean
    ): List<Tool<*>> {
        val layout = getLayout()
        val tools = collectAppropriateTools(layout, themeId, characterId, removeToolsForTheme)
        val nextLayout = layout.removeTools(tools)
        saveLayout(nextLayout)
        return tools
    }

    private fun Layout.removeTools(
        tools: List<Tool<*>>
    ): Layout {
        return tools.fold(this) { nextLayout, tool ->
            nextLayout.toolRemoved(tool.id)
        }
    }

    private fun collectAppropriateTools(
        layout: Layout,
        themeId: UUID,
        characterId: UUID,
        removeToolsForTheme: Boolean
    ): List<Tool<*>> {
        return if (removeToolsForTheme) {
            layout.getToolsIdentifiedByTheme(Theme.Id(themeId))
        } else {
            layout.getToolsIdentifiedByThemeAndCharacter(Theme.Id(themeId), Character.Id(characterId))
        }
    }

    private fun Layout.getToolsIdentifiedByTheme(
        themeId: Theme.Id
    ): List<Tool<*>> {
        return tools.filter {
            it.identifiedWithAnyThemeIdIn(setOf(themeId))
        }
    }

    private fun Layout.getToolsIdentifiedByThemeAndCharacter(
        themeId: Theme.Id,
        characterId: Character.Id
    ): List<Tool<*>> {
        return tools.filter {
            it.identifiedWithAnyThemeIdIn(setOf(themeId)) && it.identifiedWithCharacter(characterId)
        }
    }

    private suspend fun saveLayout(nextLayout: Layout) {
        context.layoutRepository.saveLayout(nextLayout)
    }

    private suspend fun getLayout() = context.layoutRepository.getLayoutForProject(projectId) ?: throw LayoutDoesNotExist()

    private suspend fun tryToDeleteCharacterArc(
        themeId: UUID,
        characterId: UUID
    ): DemoteMajorCharacter.ResponseModel {
        return try {
            suspendCoroutine<DemoteMajorCharacter.ResponseModel> {
                runBlocking {
                    deleteCharacterArc.invoke(themeId, characterId, DemoteMajorCharacterOutputContinuation(it))
                }
            }
        } catch (e: Exception) {
            throw FailedToDemoteCharacter(e)
        }
    }
}