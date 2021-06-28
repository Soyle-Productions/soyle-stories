package com.soyle.stories.domain.project

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class Project(
    override val id: Id,
    val name: NonBlankString
) : Entity<Project.Id> {

    data class Id(val uuid: UUID = UUID.randomUUID())

    fun rename(newName: NonBlankString): Project = Project(id, newName)

    companion object {
        fun startNew(name: NonBlankString) = Project(Id(), name)
    }

}