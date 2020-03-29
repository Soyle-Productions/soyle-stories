package com.soyle.stories.entities

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.common.Entity
import com.soyle.stories.project.NameCannotBeBlank
import java.util.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 2:17 PM
 */
class Project(
    override val id: Id,
    val name: String
) : Entity<Project.Id> {

    init {
        if (name.isBlank()) throw NameCannotBeBlank
    }

    /*
     *
     * Class is now a data class to resolve #7
     * https://github.com/b-camphart/soyle-studio-core/issues/7
     *
     */
    data class Id(val uuid: UUID)

    fun rename(newName: String): Either<*, Project> {
        return try {
            Project(id, newName).right()
        } catch (e: Exception) {
            e.left()
        }
    }

    companion object {
        fun startNew(name: String): Either<Throwable, Project> {
            val projectId = Id(UUID.randomUUID())
            return try {
                Project(projectId, name).right()
            } catch (e: Exception) {
                e.left()
            }
        }
    }

}