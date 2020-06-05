package com.soyle.stories.layout.usecases.getSavedLayout

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.defaultLayout
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.usecases.toOpenTool
import com.soyle.stories.layout.usecases.toOpenWindow
import java.util.*

class GetSavedLayoutUseCase(
    private val layoutRepository: LayoutRepository
) : GetSavedLayout {


    override suspend fun invoke(projectId: UUID, outputPort: GetSavedLayout.OutputPort) {
        val layout = getLayout(projectId)

        GetSavedLayout.ResponseModel(
          layout.id.uuid, layout.windows.mapNotNull { it.toOpenWindow() }, layout.fixedTools.mapNotNull { it.toOpenTool() }
        ).let(outputPort::receiveGetSavedLayoutResponse)
    }

    private suspend fun getLayout(projectId: UUID): Layout
    {
        val storedLayout = layoutRepository.getLayoutForProject(Project.Id(projectId))
        return if (storedLayout == null) {
            val newLayout = defaultLayout(Project.Id(projectId), Layout.Id())
            layoutRepository.saveLayout(newLayout)
            newLayout
        } else storedLayout
    }

}