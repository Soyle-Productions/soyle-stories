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
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.scene.charactersInScene.assignRole.AssignRoleToCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CoverArcSectionsInSceneController
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.setDesire.SetCharacterDesireInSceneController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.sceneFrame.SetSceneFrameValueController
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
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


    fun givenSceneHasConflict(scene: Scene, expectedConflict: String) {
        if (scene.conflict.value == expectedConflict) return
        setSceneConflict(scene.id, expectedConflict)
    }

    private fun setSceneConflict(sceneId: Scene.Id, conflict: String) {
        projectScope.get<SetSceneFrameValueController>().setSceneConflict(sceneId, conflict)
    }


    fun givenSceneHasResolution(scene: Scene, expectedResolution: String) {
        if (scene.resolution.value == expectedResolution) return
        setSceneResolution(scene.id, expectedResolution)
    }

    private fun setSceneResolution(sceneId: Scene.Id, resolution: String) {
        projectScope.get<SetSceneFrameValueController>().setSceneResolution(sceneId, resolution)
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
        scene.isCharacterArcSectionCovered(section.id)

    private fun coverArcSectionInScene(scene: Scene, section: CharacterArcSection) {
        projectScope.get<CoverArcSectionsInSceneController>().coverCharacterArcSectionInScene(
            scene.id.uuid.toString(),
            section.characterId.uuid.toString(),
            listOf(section.id.uuid.toString())
        )
    }

    fun givenCharacterHasRole(scene: Scene, character: Character, role: String)
    {
        if (!characterHasRole(scene, character, role)) assignRoleToCharacter(scene, character, role)
    }

    private fun characterHasRole(scene: Scene, character: Character, role: String): Boolean
    {
        val characterInScene = scene.includedCharacters.get(character.id)
        return when (role) {
            "Inciting Character" -> characterInScene?.roleInScene == RoleInScene.IncitingCharacter
            else -> characterInScene?.roleInScene == RoleInScene.OpponentCharacter
        }
    }

    private fun assignRoleToCharacter(scene: Scene, character: Character, role: String)
    {
        projectScope.get<AssignRoleToCharacterInSceneController>().assignRole(scene.id, character.id, when (role) {
            "Inciting Character" -> RoleInScene.IncitingCharacter
            else -> RoleInScene.OpponentCharacter
        })
    }

    fun givenCharacterHasDesire(scene: Scene, character: Character, desire: String)
    {
        if (!characterHasDesire(scene, character, desire)) setCharacterDesireInScene(scene, character, desire)
    }

    private fun characterHasDesire(scene: Scene, character: Character, desire: String): Boolean
    {
        val characterInScene = scene.includedCharacters.get(character.id)
        return characterInScene?.desire == desire
    }

    private fun setCharacterDesireInScene(scene: Scene, character: Character, desire: String)
    {
        projectScope.get<SetCharacterDesireInSceneController>().setDesire(scene.id, character.id, desire)
    }

    fun givenLocationUsedInScene(scene: Scene, location: Location)
    {
        if (! scene.settings.containsEntityWithId(location.id)) useLocationInScene(scene, location)
    }

    fun useLocationInScene(scene: Scene, location: Location)
    {
        projectScope.get<LinkLocationToSceneController>().linkLocationToScene(scene.id, location.id)
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