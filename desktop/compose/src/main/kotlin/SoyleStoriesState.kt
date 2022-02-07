package com.soyle.stories.desktop.view

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.soyle.stories.desktop.view.di.koin
import com.soyle.stories.project.closeProject.ClosedProjectNotifier
import com.soyle.stories.project.closeProject.ClosedProjectReceiver
import com.soyle.stories.project.list.ListOpenProjectsController
import com.soyle.stories.project.openProject.ProjectOpenedNotifier
import com.soyle.stories.project.openProject.ProjectOpenedReceiver
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import kotlinx.coroutines.Job
import org.koin.core.context.GlobalContext.get

sealed class SoyleStoriesState {
    abstract val state: State<SoyleStoriesState>

    class Uninitialized(
        private val listOpenProjects: ListOpenProjectsController
    ) : SoyleStoriesState() {
        private val _state = mutableStateOf<SoyleStoriesState>(this)
        override val state: State<SoyleStoriesState> = _state

        fun load(): Job {
            val job = listOpenProjects.listOpenProjects {
                _state.value = Loaded(
                    _state,
                    it.openProjects.toMutableStateList()
                )
            }
            _state.value = Loading(_state, job)
            return job
        }

    }

    class Loading(
        private val _state: MutableState<SoyleStoriesState>,
        val job: Job
    ) : SoyleStoriesState() {

        override val state: State<SoyleStoriesState> = _state

        fun cancelLoad() {
            if (!job.isCompleted) {
                job.cancel()
                _state.value = Loaded(_state, mutableStateListOf())
            }
        }

    }

    class Loaded(
        _state: MutableState<SoyleStoriesState>,
        val openProjects: SnapshotStateList<ListOpenProjects.OpenProjectItem>
    ) : SoyleStoriesState(), ProjectOpenedReceiver, ClosedProjectReceiver {
        override val state: State<SoyleStoriesState> = _state

        override suspend fun receiveOpenedProject(openedProject: OpenProject.ResponseModel) {
            openProjects.add(
                ListOpenProjects.OpenProjectItem(
                    openedProject.projectId,
                    openedProject.projectName,
                    openedProject.projectLocation
                )
            )
        }

        override suspend fun receiveClosedProject(closedProject: CloseProject.ResponseModel) {
            openProjects.removeIf { it.projectId == closedProject.projectId }
        }

        init {
            get().get<ProjectOpenedNotifier>().addListener(this)
            koin.get<ClosedProjectNotifier>().addListener(this)
        }
    }

    companion object {
        @Composable
        fun remember(
            listOpenProjects: ListOpenProjectsController = get().get()
        ): SoyleStoriesState {
            return remember { Uninitialized(listOpenProjects).state }.value
        }
    }

}