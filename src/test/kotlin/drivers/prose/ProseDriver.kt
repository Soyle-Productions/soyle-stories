package com.soyle.stories.desktop.config.drivers.prose

import com.soyle.stories.common.EntityId
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.Prose
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.prose.ProseDoesNotExist
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorState
import com.soyle.stories.prose.repositories.ProseRepository
import kotlinx.coroutines.runBlocking

class ProseDriver private constructor(private val projectScope: ProjectScope) {

    fun getProseByIdOrError(proseId: Prose.Id): Prose = getProseById(proseId) ?: throw ProseDoesNotExist(proseId)
    fun getProseById(proseId: Prose.Id): Prose? {
        val repo = projectScope.get<ProseRepository>()
        return runBlocking {
            repo.getProseById(proseId)
        }
    }

    fun givenProseMentionsEntity(prose: Prose, entityId: EntityId<*>, index: Int, length: Int) {
        ProseEditorScope(projectScope, prose.id).apply {
            get<ProseEditorState>().versionNumber.set(prose.revision)
        }.get<EditProseController>()
            .addMention(entityId, index, length)
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { ProseDriver(this) } }
        }

        operator fun invoke(workbench: WorkBench): ProseDriver = workbench.scope.get()
    }

}