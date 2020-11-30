package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.character.CharacterArcDriver
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.repositories.SceneRepository
import kotlinx.coroutines.runBlocking

class SceneDriver private constructor(private val projectScope: ProjectScope) {

    private val previouslyNamedScenes = mutableMapOf<String, Scene.Id>()

    fun getSceneByNameOrError(sceneName: String): Scene =
        getSceneByName(sceneName) ?: throw NoSuchElementException("No scene named $sceneName in project ${projectScope.projectViewModel.name}")

    fun getSceneByName(sceneName: String): Scene? {
        val sceneRepository = projectScope.get<SceneRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allScenes = runBlocking { sceneRepository.listAllScenesInProject(projectId) }
        val theme = allScenes.find { it.name.value == sceneName }
        theme?.let { previouslyNamedScenes[sceneName] = it.id }
        return theme
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { SceneDriver(this) } }
        }
        operator fun invoke(workbench: WorkBench): SceneDriver = workbench.scope.get()
    }
}