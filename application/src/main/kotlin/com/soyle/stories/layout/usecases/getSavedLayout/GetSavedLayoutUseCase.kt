package com.soyle.stories.layout.usecases.getSavedLayout

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.usecases.toActiveWindow
import com.soyle.stories.layout.usecases.toStaticTool
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 12:24 PM
 */
class GetSavedLayoutUseCase(
    private val layoutRepository: LayoutRepository
) : GetSavedLayout {


    override suspend fun invoke(projectId: UUID, outputPort: GetSavedLayout.OutputPort) {
        val storedLayout = layoutRepository.getLayoutForProject(Project.Id(projectId))
        if (storedLayout == null) {
            Layout.organizeLayout(Project.Id(projectId))
                .map {
                    runBlocking {
                        layoutRepository.saveLayout(it)
                    }
                    GetSavedLayout.ResponseModel(
                        it.id.uuid, it.windows.filter { it.isOpen }.map { it.toActiveWindow() }, it.staticTools.map { it.toStaticTool() }
                    ).let(outputPort::receiveGetSavedLayoutResponse)
                }
        } else {
            GetSavedLayout.ResponseModel(
                storedLayout.id.uuid, storedLayout.windows.filter { it.isOpen }.map { it.toActiveWindow() }, storedLayout.staticTools.map { it.toStaticTool() }
            ).let(outputPort::receiveGetSavedLayoutResponse)
        }
    }


}