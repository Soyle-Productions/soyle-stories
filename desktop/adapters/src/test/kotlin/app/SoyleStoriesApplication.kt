package com.soyle.stories.desktop.adapter.app

import com.soyle.stories.desktop.adapter.project.OpenProjectInSoyleStories
import com.soyle.stories.desktop.adapter.project.ProjectListEvent
import com.soyle.stories.desktop.adapter.project.projectsReducer
import com.soyle.stories.domain.writer.Writer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

data class SoyleStoriesApplication(
    val writer: Writer.Id,
    val openProjects: List<OpenProjectInSoyleStories> = listOf()
)

fun appReducer(state: SoyleStoriesApplication, action: ApplicationEvent) =
    when (action) {
        is ProjectListEvent -> state.copy(openProjects = projectsReducer(state.openProjects, action))
        else -> state
    }

class Store(writer: Writer.Id, mainContext: CoroutineContext) {

    private val coroutineScope = CoroutineScope(mainContext)

    private var currentState = SoyleStoriesApplication(writer)
    private val mutex = Mutex()

    fun <T : ApplicationEvent> dispatch(action: T): T {
        coroutineScope.launch {
            mutex.withLock {
                currentState = appReducer(currentState, action)
            }
        }
        return action
    }

    protected fun finalize() { coroutineScope.cancel() }

}

val store = Store(Writer.Id(), Dispatchers.Main)

