/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 12:38 PM
 */
package com.soyle.stories.workspace

import java.util.*

abstract class ProjectException : Exception() {
    abstract val location: String
}

/**
 * ExpectedProjectExceptions involve projects that were previously stored in the workspace.  These are projects the
 * system expects to find at the location.  When the project is different or not there, an ExpectedProjectException is
 * returned
 */
sealed class ExpectedProjectException : ProjectException() {
    /**
     * the id of the project that was expected to be found at this location
     */
    abstract val expectedProjectId: UUID
    /**
     * the name of the project that was expected to be found at this location
     */
    abstract val expectedProjectName: String
}


class ExpectedProjectDoesNotExistAtLocation(
    /**
     * the id of the project that was expected to be found at this location
     */
    override val expectedProjectId: UUID,
    /**
     * the name of the project that was expected to be found at this location
     */
    override val expectedProjectName: String,
    override val location: String
) : ExpectedProjectException()

class UnexpectedProjectExistsAtLocation(
    /**
     * the id of the project that is was found at this location
     */
    val foundProjectId: UUID,
    /**
     * the name of the project that is was found at this location
     */
    val foundProjectName: String,
    /**
     * the id of the project that was expected to be found at this location
     */
    override val expectedProjectId: UUID,
    /**
     * the name of the project that was expected to be found at this location
     */
    override val expectedProjectName: String,
    override val location: String
) : ExpectedProjectException()



class ProjectDoesNotExistAtLocation(
    override val location: String
) : ProjectException() {
    override val message: String? = "Project doesn't Exist at Location $location"
}

class UnexpectedProjectAlreadyOpenAtLocation(
    override val location: String,
    /**
     * the id of the project that is was found at this location
     */
    val foundProjectId: UUID,
    /**
     * the name of the project that is was found at this location
     */
    val foundProjectName: String,
    /**
     * the id of the project that is currently open in the workspace from this location
     */
    val openProjectId: UUID,
    /**
     * the name of the project that is currently open in the workspace from this location
     */
    val openProjectName: String
) : ProjectException()

class ProjectAlreadyOpen(
    /**
     * the id of the project found at this location and is already open in the workspace
     */
    val projectId: UUID,
    /**
     * the name of the project found at this location and is already open in the workspace
     */
    val projectName: String,
    override val location: String
) : ProjectException()

class ProjectNotOpen(
    /**
     * the id of the project that is not open in the workspace
     */
    val projectId: UUID
) : Exception()