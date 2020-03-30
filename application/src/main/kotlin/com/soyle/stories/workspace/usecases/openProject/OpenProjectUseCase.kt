/**
 * Created by Brendan
 * Date: 2/13/2020
 * Time: 4:37 PM
 */
package com.soyle.stories.workspace.usecases.openProject

import com.soyle.stories.translators.asProjectFile
import com.soyle.stories.workspace.ProjectAlreadyOpen
import com.soyle.stories.workspace.ProjectDoesNotExistAtLocation
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.UnexpectedProjectAlreadyOpenAtLocation
import com.soyle.stories.workspace.entities.Workspace
import com.soyle.stories.workspace.repositories.ProjectRepository
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.valueobjects.ProjectFile

class OpenProjectUseCase(
    private val workerId: String,
    private val workSpaceRepository: WorkspaceRepository,
    private val projectRepository: ProjectRepository,
    private val closeProject: CloseProject
) : OpenProject {

    override suspend fun invoke(location: String, outputPort: OpenProject.OutputPort) {
        val response = try {
            openProject(location)
        } catch (c: ConfirmationRequired) {
            respondWithProjectAndConfirmationRequired(c.projectFile)
        } catch (e: ProjectException) {
            return outputPort.receiveOpenProjectFailure(e)
        }
        outputPort.receiveOpenProjectResponse(response)
    }

    override suspend fun forceOpenProject(location: String, outputPort: OpenProject.OutputPort) {
        val response = try {
            openProject(location)
        } catch (c: ConfirmationRequired) {
            addProjectFileToWorkspace(c.projectFile, c.workspace)
            respondWith(c.projectFile)
        } catch (e: ProjectException) {
            return outputPort.receiveOpenProjectFailure(e)
        }
        outputPort.receiveOpenProjectResponse(response)
    }

    override suspend fun replaceOpenProject(location: String, outputPort: OpenProject.OutputPort) {
        val response = try {
            openProject(location)
        } catch (c: ConfirmationRequired) {
            closeProject.invoke(
                c.workspace.openProjects.first().projectId.uuid,
                CloseProjectOutputContinuation(this, outputPort, location)
            )
            return
        } catch (e: ProjectException) {
            return outputPort.receiveOpenProjectFailure(e)
        }
        outputPort.receiveOpenProjectResponse(response)
    }

    private suspend fun openProject(location: String): OpenProject.ResponseModel {
        val projectFile = getProjectFileAtLocation(location)
        val workspace = getOrMakeWorkspaceForWorker(workerId)
        openProjectInWorkspace(projectFile, workspace)
        return respondWith(projectFile)
    }

    private suspend fun getProjectFileAtLocation(location: String): ProjectFile {
        return projectRepository.getProjectAtLocation(location)?.asProjectFile(location)
            ?: throw ProjectDoesNotExistAtLocation(location)
    }

    private suspend fun getOrMakeWorkspaceForWorker(workerId: String): Workspace {
        return workSpaceRepository.getWorkSpaceForWorker(workerId) ?: return makeWorkspaceForWorker(workerId)
    }

    private suspend fun openProjectInWorkspace(projectFile: ProjectFile, workspace: Workspace) {
        verifyProjectFileCanBeAddedToWorkspace(projectFile, workspace)
        addProjectFileToWorkspace(projectFile, workspace)
    }

    private fun verifyProjectFileCanBeAddedToWorkspace(projectFile: ProjectFile, workspace: Workspace) {
        verifyProjectWillNotOverrideProjectAlreadyOpenAtLocationInWorkspace(projectFile, workspace)
        verifyProjectNotAlreadyOpenInWorkspace(projectFile, workspace)
        requireConfirmationIfWorkspaceHasExactlyOneProject(projectFile, workspace)
    }

    private suspend fun addProjectFileToWorkspace(projectFile: ProjectFile, workspace: Workspace) {
        workspace.addProject(projectFile).fold(
            { throw it },
            { workSpaceRepository.updateWorkspace(it) }
        )
    }

    private fun verifyProjectWillNotOverrideProjectAlreadyOpenAtLocationInWorkspace(
        projectFile: ProjectFile,
        workspace: Workspace
    ) {
        val projectOpenAtLocation = workspace.openProjects.find { it.location == projectFile.location }
        if (projectOpenAtLocation?.projectId != null && projectOpenAtLocation.projectId != projectFile.projectId) {
            throw projectDoesNotMatchProjectOpenAtLocation(projectFile, projectOpenAtLocation)
        }
    }

    private fun verifyProjectNotAlreadyOpenInWorkspace(projectFile: ProjectFile, workspace: Workspace) {
        val openProjectWithId = workspace.openProjects.find { it.projectId == projectFile.projectId }
        if (openProjectWithId != null) {
            throw ProjectAlreadyOpen(
                projectFile.projectId.uuid,
                projectFile.projectName,
                projectFile.location
            )
        }
    }

    private fun requireConfirmationIfWorkspaceHasExactlyOneProject(projectFile: ProjectFile, workspace: Workspace) {
        if (workspace.openProjects.size == 1) {
            throw ConfirmationRequired(projectFile, workspace)
        }
    }

    private suspend fun makeWorkspaceForWorker(workerId: String): Workspace {
        val newWorkspace = Workspace(workerId, listOf())
        workSpaceRepository.addNewWorkspace(newWorkspace)
        return newWorkspace
    }

    private fun projectDoesNotMatchProjectOpenAtLocation(projectFile: ProjectFile, projectOpenAtLocation: ProjectFile) =
        UnexpectedProjectAlreadyOpenAtLocation(
            projectFile.location,
            projectFile.projectId.uuid,
            projectFile.projectName,
            projectOpenAtLocation.projectId.uuid,
            projectOpenAtLocation.projectName
        )

    private fun respondWith(projectFile: ProjectFile) = OpenProject.ResponseModel(
        projectFile.projectId.uuid, projectFile.projectName, projectFile.location, false
    )

    private fun respondWithProjectAndConfirmationRequired(projectFile: ProjectFile) = OpenProject.ResponseModel(
        projectFile.projectId.uuid, projectFile.projectName, projectFile.location, true
    )

    private class ConfirmationRequired(val projectFile: ProjectFile, val workspace: Workspace) : Exception()

}