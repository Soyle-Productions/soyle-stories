package com.soyle.stories.project.usecases.startnewLocalProject

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.*
import com.soyle.stories.usecase.project.startNewProject.StartNewProject
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.valueobjects.ProjectFile
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine

class StartNewLocalProjectUseCase(
    private val fileRepository: FileRepository,
    private val startNewProjectUseCase: StartNewProject,
    private val openProjectUseCase: OpenProject
) : StartNewLocalProject {

    override suspend fun invoke(request: StartNewLocalProject.RequestModel, output: StartNewLocalProject.OutputPort) {
        val response = try {
            startNewLocalProject(request)
        } catch (l: LocalProjectException) {
            return output.receiveStartNewLocalProjectFailure(l)
        } catch (p: ProjectException) {
            return output.receiveOpenProjectFailure(p)
        } catch (t: Throwable) {
            throw t
        }
        output.receiveOpenProjectResponse(response)
    }

    private suspend fun startNewLocalProject(request: StartNewLocalProject.RequestModel): OpenProject.ResponseModel {
        validateRequest(request)
        val result = startNewProject(request.projectName)
        val projectFile = getProjectFileFromResponseModelInDirectory(result, request.directory)
        createProjectFile(projectFile)
        return tryToOpenProjectAtLocation(projectFile.location)
    }

    private suspend fun validateRequest(request: StartNewLocalProject.RequestModel) {
        validateDirectory(request.directory)
        validateFileName(request.projectName.value, request.directory)
    }

    private suspend fun startNewProject(projectName: NonBlankString): StartNewProject.ResponseModel {
        return suspendCancellableCoroutine { continuation ->
            runBlocking {
                startNewProjectUseCase.invoke(projectName, StartNewProjectOutputContinuation(continuation))
                continuation.cancel(NeverStartedNewProject())
            }
        }
    }

    private fun getProjectFileFromResponseModelInDirectory(
        response: StartNewProject.ResponseModel,
        directory: String
    ): ProjectFile {
        return ProjectFile(
            Project.Id(response.projectId),
            response.projectName,
            getFilePath(directory, response.projectName)
        )
    }

    private suspend fun createProjectFile(projectFile: ProjectFile) {
        fileRepository.createFile(projectFile)
    }

    private suspend fun tryToOpenProjectAtLocation(projectLocation: String): OpenProject.ResponseModel {
        return suspendCancellableCoroutine<OpenProject.ResponseModel> { continuation ->
            runBlocking {
                openProjectUseCase.invoke(projectLocation, OpenProjectOutputContinuation(continuation))
                continuation.cancel(NeverOpenedProject())
            }
        }
    }

    private suspend fun validateDirectory(directory: String) {
        if (!fileRepository.doesDirectoryExist(directory)) {
            throw DirectoryDoesNotExist(directory)
        }
    }

    private suspend fun validateFileName(projectName: String, directory: String) {
        val filePath = getFilePath(directory, projectName)
        if (fileRepository.doesFileExist(filePath)) {
            throw FileAlreadyExists(filePath)
        }
    }

    private fun getFilePath(directory: String, projectName: String) = "$directory\\$projectName.stry"
}