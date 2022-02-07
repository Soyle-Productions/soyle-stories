package com.soyle.stories.project.startNewProject

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.openProject.OpenProjectController
import com.soyle.stories.usecase.project.startNewProject.StartNewProject
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.valueobjects.ProjectFile
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.CoroutineContext

interface StartProjectController {

    fun startProject(directory: String, name: NonBlankString): Job

    fun startProject(): Job


    class Implementaton(
        private val mainContext: CoroutineContext,
        private val ioContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val startProjectPrompt: StartProjectPrompt,

        private val startNewProject: StartNewProject,
        private val startNewProjectOutput: StartNewProject.OutputPort,
        private val fileRepository: FileRepository,

        private val openProjectController: OpenProjectController
    ) : StartProjectController, CoroutineScope by CoroutineScope(mainContext) {

        override fun startProject(directory: String, name: NonBlankString): Job {
            TODO("Not yet implemented")
        }

        override fun startProject(): Job = launch {
            val (location, name) = collectValidLocation() ?: return@launch
            withContext(asyncContext) {
                startNewProject.invoke(name, object : StartNewProject.OutputPort {
                    override suspend fun receiveStartNewProjectResponse(response: StartNewProject.ResponseModel) {
                        fileRepository.createFile(ProjectFile(Project.Id(response.projectId), response.projectName, location))
                        startNewProjectOutput.receiveStartNewProjectResponse(response)
                    }

                    override fun receiveStartNewProjectFailure(failure: Throwable) {
                        startNewProjectOutput.receiveStartNewProjectFailure(failure)
                    }
                })
                startProjectPrompt.close()
                delay(100)
                openProjectController.openProject(location)
            }
        }

        private tailrec suspend fun collectValidDirectory(previousAttempt: String? = null, errorMessage: String? = null): String? {
            val directoryAttempt = startProjectPrompt.requestDirectory(previousAttempt, errorMessage) ?: return null
            val file = File(directoryAttempt)
            val exists = withContext(ioContext) { file.exists() }
            val isDirectory = withContext(ioContext) { file.isDirectory }
            if (exists && isDirectory) return directoryAttempt

            return when {
                ! exists -> collectValidDirectory(directoryAttempt, "Folder Does Not Exist")
                else -> collectValidDirectory(directoryAttempt, "$directoryAttempt is not a Folder")
            }
        }

        private tailrec suspend fun collectValidLocation(
            previousDirectory: String? = null,
            previousName: String? = null,
            previousError: String? = null
        ): Pair<String, NonBlankString>? {
            val (directory, name) = coroutineScope {
                awaitAll(
                    async { collectValidDirectory(previousDirectory, previousError) },
                    async { startProjectPrompt.requestProjectName(previousName, previousError) }
                )
            }

            if (directory == null || name == null) return null

            val location = directory.toString() + File.separator + name.toString() + ".stry"
            val file = File(location)
            val exist = withContext(ioContext) { file.exists() }
            if (! exist) return location to (name as NonBlankString)

            return collectValidLocation(directory.toString(), name.toString(), "File already exists at $location")
        }



    }

}