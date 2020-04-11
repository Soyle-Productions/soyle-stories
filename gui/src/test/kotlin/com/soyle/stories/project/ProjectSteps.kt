package com.soyle.stories.project

import com.soyle.stories.entities.Project
import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.project.UserInterfaceInputState.Field
import com.soyle.stories.project.doubles.InMemoryFileRepository
import com.soyle.stories.project.doubles.InMemoryWorkspaceRepository
import com.soyle.stories.project.drivers.UserDriver
import com.soyle.stories.project.eventbus.OpenProjectNotifier
import com.soyle.stories.project.eventbus.ProjectEvents
import com.soyle.stories.project.eventbus.RequestCloseProjectNotifier
import com.soyle.stories.project.eventbus.StartNewProjectNotifier
import com.soyle.stories.project.projectList.*
import com.soyle.stories.project.usecases.startNewProject.StartNewProjectUseCase
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProjectUseCase
import com.soyle.stories.workspace.entities.Workspace
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.repositories.ProjectRepository
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import com.soyle.stories.workspace.usecases.closeProject.CloseProjectUseCase
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjectsUseCase
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.openProject.OpenProjectUseCase
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProjectUseCase
import com.soyle.stories.workspace.valueobjects.ProjectFile
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import java.io.File
import java.util.*

class ProjectSteps : En {
    companion object {
        private val workspaceProjectDirectory = "C:\\Users\\Default\\Documents"
        private fun projectFile(): ProjectFile {
            val name = "My epic tale ${UUID.randomUUID()})"
            val location = "$workspaceProjectDirectory\\$name.stry"
            return ProjectFile(Project.Id(UUID.randomUUID()), name, location)
        }

        private val workspaceProjectFile = projectFile()
        private val workspaceProjectId = workspaceProjectFile.projectId.uuid
        private val workspaceProjectName = workspaceProjectFile.projectName
        private val selectedProjectFile = projectFile()
        private val createdProjectFile = projectFile()
    }

    private val uiState = UserInterfaceInputState()
    private val user = UserDriver(uiState)
    private val repositoryState = RepositoryState()
    private val projectModule: ProjectModule = ProjectModule(repositoryState)

    private var actualResult: Any? = null

    init {
        Given("Application has never been run before") {
            repositoryState.storedWorkspace = null
        }

        Given("Workspace has no open projects") {
            repositoryState.previouslyOpenedProjects = listOf()
        }

        Given("Workspace has one open project") {
            repositoryState.previouslyOpenedProjects = listOf(workspaceProjectFile)
        }

        Given("The workspace project file exists") {
            val workspaceProjectFile = repositoryState.previouslyOpenedProjects.first()
            repositoryState.files = repositoryState.files + workspaceProjectFile.location
            repositoryState.projects = repositoryState.projects + (workspaceProjectFile.location to Project(workspaceProjectFile.projectId, workspaceProjectFile.projectName))
        }

        Given("The workspace project file doesn't exist") {
            val workspaceProjectFile = repositoryState.previouslyOpenedProjects.first()
            repositoryState.files = repositoryState.files - workspaceProjectFile.location
        }

        Given("User has selected a directory") {
            user.selectDirectory(workspaceProjectDirectory)
        }

        Given("The selected directory exists") {
            repositoryState.directories = repositoryState.directories + uiState.fields.getValue(Field.Directory)
        }

        Given("User has entered a name into the project name field") {
            user.enterTextIntoField(createdProjectFile.projectName, Field.ProjectName)
        }

        Given("A file with the entered name does not exist in selected directory") {
            val directory = uiState.fields.getValue(Field.Directory)
            val fileName = uiState.fields.getValue(Field.ProjectName)
            repositoryState.files = repositoryState.files - "$directory\\$fileName.stry"
        }

        Given("User has selected a file") {
            uiState.fields[Field.SelectedFile] = selectedProjectFile.location
        }
        Given("The selected file exists") {
            repositoryState.files = repositoryState.files + uiState.fields.getValue(Field.SelectedFile)
        }

        Given("The selected file is a project file") {
            val location = uiState.fields.getValue(Field.SelectedFile)
            val fileName = File(location).nameWithoutExtension
            repositoryState.projects = repositoryState.projects +
                    (location to Project(selectedProjectFile.projectId, fileName))
        }

        Given("Welcome screen has been opened") {
            repositoryState.previouslyOpenedProjects = listOf()
            projectModule.startApplication()
        }

        Given("One project has been opened") {
            repositoryState.previouslyOpenedProjects = mutableListOf(workspaceProjectFile)
            repositoryState.files = repositoryState.files + workspaceProjectFile.location
            repositoryState.projects = repositoryState.projects + (workspaceProjectFile.location to Project(workspaceProjectFile.projectId, workspaceProjectFile.projectName))
            projectModule.startApplication()
        }

        Given("Two projects have been opened") {
            val secondFile = projectFile()
            repositoryState.previouslyOpenedProjects = mutableListOf(workspaceProjectFile, secondFile)
            repositoryState.files = repositoryState.files + listOf(workspaceProjectFile.location, secondFile.location)
            repositoryState.projects = repositoryState.projects + (workspaceProjectFile.location to Project(workspaceProjectFile.projectId, workspaceProjectFile.projectName)) + (secondFile.location to Project(secondFile.projectId, secondFile.projectName))
            projectModule.startApplication()
        }

        Given("The application has started") {
            projectModule.startApplication()
        }

        When("Application is started") {
            actualResult = projectModule.startApplication()
        }

        When("Application is closed") {

        }

        When("User starts a new project") {
            actualResult = projectModule.startNewProject(uiState.fields.getValue(Field.Directory), uiState.fields.getValue(Field.ProjectName))
        }

        When("The selected file is opened") {
            actualResult = projectModule.openProject(uiState.fields.getValue(Field.SelectedFile))
        }

        When("The open in current window option is selected") {
            actualResult = projectModule.replaceCurrentProject(uiState.fields.getValue(Field.SelectedFile))
        }

        When("The open in new window option is selected") {
            actualResult = projectModule.forceOpenProject(uiState.fields.getValue(Field.SelectedFile))
        }

        Then("Welcome screen should be open") {
            val actualResult = actualResult as ProjectListViewModel
            Assertions.assertTrue(actualResult.isWelcomeScreenVisible)
        }

        Then("Welcome screen should not be open") {
            val actualResult = actualResult as ProjectListViewModel
            Assertions.assertFalse(actualResult.isWelcomeScreenVisible)
        }

        Then("The workspace project should be open") {
            val actualResult = actualResult as ProjectListViewModel
            val openProject = repositoryState.previouslyOpenedProjects.first()
            Assertions.assertEquals(openProject.projectId.uuid, actualResult.openProjects.single().projectId)
        }

        Then("Created project should be open") {
            val actualResult = actualResult as ProjectListViewModel
            Assertions.assertEquals(uiState.fields.getValue(Field.ProjectName), actualResult.openProjects.single().name)
        }

        Then("Created project should not be open") {
            val actualResult = actualResult as ProjectListViewModel
            val createdProjectName = uiState.fields.getValue(Field.ProjectName)
            assertNull(actualResult.openProjects.find { it.name == createdProjectName })
        }

        Then("Project failure dialog should be open") {
            val actualResult = actualResult as ProjectListViewModel
            Assertions.assertTrue(actualResult.isFailedProjectDialogVisible)
        }

        Then("Failed project location should be listed") {
            val actualResult = actualResult as ProjectListViewModel
            Assertions.assertEquals(workspaceProjectFile.location, actualResult.failedProjects.single().location)
        }

        Then("Failed project name should be listed") {
            val actualResult = actualResult as ProjectListViewModel
            Assertions.assertEquals(workspaceProjectFile.projectName, actualResult.failedProjects.single().name)
        }

        Then("Open project option dialog should be open") {
            val actualResult = actualResult as ProjectListViewModel
            Assertions.assertTrue(actualResult.isOpenProjectOptionsDialogOpen)
        }

        Then("Open project option dialog should not be open") {
            val actualResult = actualResult as ProjectListViewModel
            Assertions.assertFalse(actualResult.isOpenProjectOptionsDialogOpen)
        }

        Then("The selected file should be open") {
            val actualResult = actualResult as ProjectListViewModel
            val selectedFile = uiState.fields.getValue(Field.SelectedFile)
            assertNotNull(actualResult.openProjects.find { it.location == selectedFile })
        }

        Then("The selected file should not be open") {
            val actualResult = actualResult as ProjectListViewModel
            val selectedFile = uiState.fields.getValue(Field.SelectedFile)
            assertNull(actualResult.openProjects.find { it.location == selectedFile })
        }

        Then("First open project should not be open") {
            val actualResult = actualResult as ProjectListViewModel
            val openProject = workspaceProjectFile
            assertNull(actualResult.openProjects.find { it.projectId == openProject.projectId.uuid })
        }

        Then("First open project should be open") {
            val actualResult = actualResult as ProjectListViewModel
            val openProject = repositoryState.previouslyOpenedProjects.first()
            assertNotNull(actualResult.openProjects.find { it.projectId == openProject.projectId.uuid })
        }

        Then("First two projects should be open") {
            val actualResult = actualResult as ProjectListViewModel
            val firstProject = repositoryState.previouslyOpenedProjects.first()
            val secondProject = repositoryState.previouslyOpenedProjects.component2()
            assertNotNull(actualResult.openProjects.find { it.projectId == firstProject.projectId.uuid })
            assertNotNull(actualResult.openProjects.find { it.projectId == secondProject.projectId.uuid })
        }


    }
}


class ProjectModule(
    context: RepositoryState
) {

    private val projectListView = object : ProjectListView {
        var vm: ProjectListViewModel? = null
        override fun update(update: ProjectListViewModel?.() -> ProjectListViewModel) {
            vm = vm.update()
        }
        override fun updateOrInvalidated(update: ProjectListViewModel.() -> ProjectListViewModel) {
            vm = vm?.update()
        }
    }
    private val closeProjectNotifier = RequestCloseProjectNotifier()
    private val openProjectNotifier = OpenProjectNotifier(closeProjectNotifier)
    private val startNewProjectNotifier = StartNewProjectNotifier(openProjectNotifier)
    private val projectListPresenter = ProjectListPresenter(
        projectListView,
      object : ProjectEvents {
          override val closeProject: Notifier<RequestCloseProject.OutputPort>
              get() = closeProjectNotifier
          override val openProject: Notifier<OpenProject.OutputPort>
              get() = openProjectNotifier
          override val startNewProject: Notifier<StartNewLocalProject.OutputPort>
              get() = startNewProjectNotifier
      }
    )
    private val closeProject = CloseProjectUseCase(
        RepositoryState.SYSTEM_USER_ID,
        context.workspaceRepo
    )
    private val openProject = OpenProjectUseCase(
        RepositoryState.SYSTEM_USER_ID,
        context.workspaceRepo,
        context.workspaceProjectRepository,
        closeProject
    )
    private val projectListViewListener: ProjectListViewListener = ProjectListController(
        ListOpenProjectsUseCase(
            RepositoryState.SYSTEM_USER_ID,
            context.workspaceRepo,
            context.workspaceProjectRepository
        ),
        projectListPresenter,
        closeProject,
        RequestCloseProjectUseCase(
            RepositoryState.SYSTEM_USER_ID,
            CloseProjectUseCase(
                RepositoryState.SYSTEM_USER_ID,
                context.workspaceRepo
            ),
            context.workspaceRepo
        ),
        closeProjectNotifier,
        StartNewLocalProjectUseCase(
            context.fileRepo,
            StartNewProjectUseCase(
                context.projectRepository
            ),
            openProject
        ),
        startNewProjectNotifier,
        openProject,
        openProjectNotifier
    )

    fun startApplication(): ProjectListViewModel {
        runBlocking {
            projectListViewListener.startApplicationWithParameters(emptyList())
        }
        return projectListView.vm!!
    }

    fun startNewProject(directory: String, name: String): ProjectListViewModel {
        runBlocking {
            projectListViewListener.startNewProject(directory, name)
        }
        return projectListView.vm!!
    }

    fun openProject(filePath: String): ProjectListViewModel {
        runBlocking {
            projectListViewListener.openProject(filePath)
        }
        return projectListView.vm!!
    }

    fun replaceCurrentProject(filePath: String): ProjectListViewModel {
        runBlocking {
            projectListViewListener.replaceCurrentProject(filePath)
        }
        return projectListView.vm!!
    }

    fun forceOpenProject(filePath: String): ProjectListViewModel {
        runBlocking {
            projectListViewListener.forceOpenProject(filePath)
        }
        return projectListView.vm!!
    }

}

class UserInterfaceInputState {

    val fields = mutableMapOf<Field, String>()

    enum class Field {
        ProjectName,
        Directory,
        SelectedFile
    }

}

class RepositoryState {

    companion object {
        const val SYSTEM_USER_ID = "User's name"
    }

    private val inMemoryWorkspaceRepo = InMemoryWorkspaceRepository()
    val workspaceRepo: WorkspaceRepository = inMemoryWorkspaceRepo
    var storedWorkspace: Workspace?
        get() = inMemoryWorkspaceRepo.workspaces[SYSTEM_USER_ID]
        set(value) {
            if (value == null) {
                inMemoryWorkspaceRepo.workspaces.remove(SYSTEM_USER_ID)
            } else {
                inMemoryWorkspaceRepo.workspaces[SYSTEM_USER_ID] = value
            }
        }
    var previouslyOpenedProjects: List<ProjectFile>
        get() = inMemoryWorkspaceRepo.workspaces[SYSTEM_USER_ID]?.openProjects ?: emptyList()
        set(value) {
            inMemoryWorkspaceRepo.workspaces[SYSTEM_USER_ID] = Workspace(SYSTEM_USER_ID, value)
        }

    private val inMemoryFileRepo = InMemoryFileRepository()
    val fileRepo: FileRepository = inMemoryFileRepo
    var files: Set<String>
        get() = inMemoryFileRepo.files.toSet()
        set(value) {
            inMemoryFileRepo.files.clear()
            inMemoryFileRepo.files.addAll(value)
        }
    var directories: Set<String>
        get() = inMemoryFileRepo.directories.toSet()
        set(value) {
            inMemoryFileRepo.directories.clear()
            inMemoryFileRepo.directories.addAll(value)
        }

    val workspaceProjectRepository: ProjectRepository = inMemoryFileRepo
    val projectRepository: com.soyle.stories.project.repositories.ProjectRepository = inMemoryFileRepo
    var projects: Map<String, Project>
        get() = inMemoryFileRepo.projects.toMap()
        set(value) {
            inMemoryFileRepo.projects.clear()
            inMemoryFileRepo.projects.putAll(value)
        }
}