package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.prose.ProseDriver
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.ProseContent
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.scene.coverArcSectionsInScene.CoverArcSectionsInSceneController
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.scene.trackSymbolInScene.PinSymbolToSceneController
import com.soyle.stories.usecase.scene.SceneRepository
import kotlinx.coroutines.runBlocking

class SceneDriver private constructor(private val projectScope: ProjectScope) {

    private val previouslyNamedScenes = mutableMapOf<String, MutableSet<Scene.Id>>().withDefault { mutableSetOf() }

    fun givenScene(sceneName: String): Scene =
        getSceneByName(sceneName) ?: createScene(sceneName).run { getSceneByNameOrError(sceneName) }

    fun getSceneByNameOrError(sceneName: String): Scene =
        getSceneByName(sceneName)
            ?: throw NoSuchElementException("No scene named $sceneName in project ${projectScope.projectViewModel.name}")

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

    private fun createScene(sceneName: String) {
        projectScope.get<CreateNewSceneController>().createNewScene(NonBlankString.create(sceneName)!!)
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

    fun givenCharacterIncludedInScene(scene: Scene, character: Character, motivation: String? = null) {
        if (!scene.includesCharacter(character.id)) includeCharacterInScene(scene, character)
        if (motivation != null) {
            if (getSceneByNameOrError(scene.name.value).getMotivationForCharacter(character.id)!!.motivation != motivation) {
                setCharacterMotivationInScene(scene, character, motivation)
            }
        }
    }

    private fun includeCharacterInScene(scene: Scene, character: Character) {
        projectScope.get<IncludeCharacterInSceneController>().includeCharacterInScene(
            scene.id.uuid.toString(),
            character.id.uuid.toString()
        )
    }

    private fun setCharacterMotivationInScene(scene: Scene, character: Character, motivation: String) {
        projectScope.get<SetMotivationForCharacterInSceneController>().setMotivationForCharacter(
            scene.id.uuid.toString(),
            character.id.uuid.toString(),
            motivation
        )
    }

    fun givenSceneCoversArcSection(scene: Scene, section: CharacterArcSection) =
        if (!doesSceneCoverArcSection(scene, section)) coverArcSectionInScene(scene, section) else Unit

    fun doesSceneCoverArcSection(scene: Scene, section: CharacterArcSection): Boolean =
        scene.coveredArcSectionIds.contains(section.id)

    private fun coverArcSectionInScene(scene: Scene, section: CharacterArcSection) {
        projectScope.get<CoverArcSectionsInSceneController>().coverCharacterArcSectionInScene(
            scene.id.uuid.toString(),
            section.characterId.uuid.toString(),
            listOf(section.id.uuid.toString())
        )
    }

    fun givenLocationUsedInScene(scene: Scene, location: Location)
    {
        if (! scene.settings.contains(location.id)) useLocationInScene(scene, location)
    }

    fun useLocationInScene(scene: Scene, location: Location)
    {
        projectScope.get<LinkLocationToSceneController>().linkLocationToScene(scene.id.uuid.toString(), location.id.uuid.toString())
    }

    fun givenSceneHasProse(scene: Scene, proseParagraphs: List<String>) {
        val prose = ProseDriver(projectScope.get()).getProseByIdOrError(scene.proseId)
        ProseEditorScope(projectScope, prose.id, { _, _ -> }, {}) { _, _ -> }
            .get<EditProseController>()
            .updateProse(scene.proseId, listOf(ProseContent(proseParagraphs.joinToString("\n"), null)))
    }

    fun givenSceneProseMentionsEntity(scene: Scene, entityId: MentionedEntityId<*>, index: Int, length: Int) {
        val prose = ProseDriver(projectScope.get()).getProseByIdOrError(scene.proseId)
        ProseDriver(projectScope.get()).givenProseMentionsEntity(prose, entityId, index, length)
    }

    fun givenSceneProseMentionsEntity(scene: Scene, entityId: MentionedEntityId<*>, name: String) {
        val prose = ProseDriver(projectScope.get()).getProseByIdOrError(scene.proseId)
        ProseDriver(projectScope.get()).givenProseMentionsEntity(prose, entityId, name)
    }

    fun givenSceneProseDoesNotMention(scene: Scene, mentionText: String) {
        val proseDriver = ProseDriver(projectScope.get())
        val prose = proseDriver.getProseByIdOrError(scene.proseId)
        proseDriver.givenProseDoesNotMention(prose, mentionText)
    }

    fun givenSymbolPinnedInScene(scene: Scene, theme: Theme, symbol: Symbol)
    {
        projectScope.get<PinSymbolToSceneController>().pinSymbolToScene(scene.id, symbol.id)
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { SceneDriver(this) } }
        }

        operator fun invoke(workbench: WorkBench): SceneDriver = workbench.scope.get()
    }
}