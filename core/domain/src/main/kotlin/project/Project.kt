package com.soyle.stories.domain.project

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.project.changes.ProjectChange
import com.soyle.stories.domain.project.changes.ProjectRenamed
import com.soyle.stories.domain.project.changes.ProjectStarted
import com.soyle.stories.domain.project.exceptions.ProjectAlreadyNamed
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class Project(
    override val id: Id,
    val name: NonBlankString
) : Entity<Project.Id> {

    data class Id(val uuid: UUID = UUID.randomUUID())

    companion object {
        fun startNew(name: NonBlankString): ProjectUpdate<ProjectStarted> {
            val id = Id()
            return ProjectUpdate.Successful(
                Project(id, name),
                ProjectStarted(id, name.value)
            )
        }
    }

    fun withName(newName: NonBlankString): ProjectUpdate<ProjectRenamed> {
        if (newName == name) return noUpdate(ProjectAlreadyNamed(id, name.value))
        val change = ProjectRenamed(id, name.value, newName.value)
        return updatedBy(change)
    }

    fun <T : ProjectChange> noUpdate(reason: Throwable? = null): ProjectUpdate<T> = ProjectUpdate.UnSuccessful(this, reason)

    private fun updatedBy(change: ProjectRenamed): ProjectUpdate.Successful<ProjectRenamed> {
        return ProjectUpdate.Successful(
            Project(id, NonBlankString.create(change.newName)!!),
            change
        )
    }

}