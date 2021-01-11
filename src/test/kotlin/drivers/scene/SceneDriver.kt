package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.prose.ProseDriver
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.MentionedEntityId
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.ProseContent
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.scene.repositories.SceneRepository
import kotlinx.coroutines.runBlocking

class SceneDriver private constructor(private val projectScope: ProjectScope) {

    private val previouslyNamedScenes = mutableMapOf<String, MutableSet<Scene.Id>>().withDefault { mutableSetOf() }

    fun getSceneByNameOrError(sceneName: String): Scene =
        getSceneByName(sceneName) ?: throw NoSuchElementException("No scene named $sceneName in project ${projectScope.projectViewModel.name}")

    fun getSceneByName(sceneName: String): Scene? {
        val sceneRepository = projectScope.get<SceneRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allScenes = runBlocking { sceneRepository.listAllScenesInProject(projectId) }
        val scene = allScenes.find { it.name.value == sceneName }
        scene?.let {
            previouslyNamedScenes[sceneName] = previouslyNamedScenes.getValue(sceneName).apply { add(scene.id) }
        }
        return scene
    }

    fun getScenesAtOnePointNamed(sceneName: String): List<Scene> {
        val sceneRepository = projectScope.get<SceneRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allScenes = runBlocking {
            sceneRepository.listAllScenesInProject(projectId)
        }.associateBy { it.id }
        return previouslyNamedScenes.getValue(sceneName).mapNotNull {
            allScenes[it]
        }
    }

    fun givenSceneHasProse(scene: Scene, proseParagraphs: List<String>)
    {
        val prose = ProseDriver(projectScope.get()).getProseByIdOrError(scene.proseId)
        ProseEditorScope(projectScope, prose.id, { _, _ -> }) {}
            .get<EditProseController>()
            .updateProse(scene.proseId, listOf(ProseContent(proseParagraphs.joinToString("\n"), null)))
    }

    fun givenSceneProseMentionsEntity(scene: Scene, entityId: MentionedEntityId<*>, index: Int, length: Int)
    {
        val prose = ProseDriver(projectScope.get()).getProseByIdOrError(scene.proseId)
        ProseDriver(projectScope.get()).givenProseMentionsEntity(prose, entityId,  index, length)
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { SceneDriver(this) } }
        }
        operator fun invoke(workbench: WorkBench): SceneDriver = workbench.scope.get()
    }
}