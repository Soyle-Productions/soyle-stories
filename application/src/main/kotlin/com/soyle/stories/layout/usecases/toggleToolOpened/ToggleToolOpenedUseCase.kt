package com.soyle.stories.layout.usecases.toggleToolOpened

import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.usecases.toActiveWindow
import com.soyle.stories.layout.usecases.toStaticTool
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 5:19 PM
 */
class ToggleToolOpenedUseCase(
    private val layoutRepository: LayoutRepository
) : ToggleToolOpened {

    override suspend fun invoke(toolId: UUID, outputPort: ToggleToolOpened.OutputPort) {
        val typedToolId = Tool.Id(toolId)
        val storedLayout = layoutRepository.getLayoutContainingTool(typedToolId) ?: return
        val modifiedLayout = if (storedLayout.isToolOpen(typedToolId)) storedLayout.closeTool(typedToolId)
        else storedLayout.openTool(typedToolId)
        modifiedLayout.map {
            runBlocking {
                layoutRepository.saveLayout(it)
            }
            ToggleToolOpened.ResponseModel(
                it.id.uuid, it.windows.map { it.toActiveWindow() }, it.staticTools.map { it.toStaticTool() }
            ).let(outputPort::receiveToggleToolOpenedResponse)
        }
    }

}