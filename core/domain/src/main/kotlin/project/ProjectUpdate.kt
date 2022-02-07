package com.soyle.stories.domain.project

import com.soyle.stories.domain.entities.updates.Update
import com.soyle.stories.domain.project.changes.ProjectChange

sealed class ProjectUpdate<out C : ProjectChange> : Update<Project> {
    abstract val project: Project
    override fun component1(): Project = project

    class Successful<out C : ProjectChange> internal constructor(
        override val project: Project,
        override val change: C
    ) : Update.Successful<Project, C>, ProjectUpdate<C>() {
        override fun component2(): C = change
    }

    class UnSuccessful internal constructor(
        override val project: Project,
        override val reason: Throwable?
    ) : Update.UnSuccessful<Project>, ProjectUpdate<Nothing>() {
        operator fun component2() = reason
    }
}