package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.character.createArcSection.CreateArcSectionController
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.RenamedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.assignRole.AssignRoleToCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CoverArcSectionsInSceneController
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.ListAvailableArcSectionsToCoverInSceneController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.listAvailableCharacters.ListAvailableCharactersToIncludeInSceneController
import com.soyle.stories.scene.charactersInScene.listCharactersInScene.ListCharactersInSceneController
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneController
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneReceiver
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventController

class SceneCharactersController private constructor(
    private val listCharactersInSceneController: ListCharactersInSceneController,
    private val listAvailableCharactersToIncludeInSceneController: ListAvailableCharactersToIncludeInSceneController,
    private val includeCharacterInSceneController: IncludeCharacterInSceneController,
    private val listAvailableArcSectionsToCoverInSceneController: ListAvailableArcSectionsToCoverInSceneController,
    private val coverArcSectionsInSceneController: CoverArcSectionsInSceneController,
    private val createArcSectionController: CreateArcSectionController,
    private val removeCharacterFromStoryEventController: RemoveCharacterFromSceneController,
    private val setMotivationForCharacterInSceneController: SetMotivationForCharacterInSceneController,
    private val assignRoleToCharacterInSceneController: AssignRoleToCharacterInSceneController,
    private val presenter: SceneCharactersPresenter
) : SceneCharactersViewListener {

    interface Dependencies {
        val listCharactersInSceneController: ListCharactersInSceneController
        val listAvailableCharactersToIncludeInSceneController: ListAvailableCharactersToIncludeInSceneController
        val includeCharacterInSceneController: IncludeCharacterInSceneController
        val listAvailableArcSectionsToCoverInSceneController: ListAvailableArcSectionsToCoverInSceneController
        val coverArcSectionsInSceneController: CoverArcSectionsInSceneController
        val createArcSectionController: CreateArcSectionController
        val removeCharacterFromSceneController: RemoveCharacterFromSceneController
        val setMotivationForCharacterInSceneController: SetMotivationForCharacterInSceneController
        val assignRoleToCharacterInSceneController: AssignRoleToCharacterInSceneController

        val includedCharacterInSceneNotifier: Notifier<IncludedCharacterInSceneReceiver>
        val removedCharacterFromSceneNotifier: Notifier<RemovedCharacterFromSceneReceiver>
        val renamedCharacterInSceneNotifier: Notifier<RenamedCharacterInSceneReceiver>
        val characterArcSectionsCoveredBySceneNotifier: Notifier<CharacterArcSectionsCoveredBySceneReceiver>
        val characterArcSectionUncoveredInSceneNotifier: Notifier<CharacterArcSectionUncoveredInSceneReceiver>
        val characterRoleInSceneChangedNotifier: Notifier<CharacterRoleInSceneChangedReceiver>
    }

    constructor(
        dependencies: Dependencies,
        view: View.Nullable<SceneCharactersViewModel>
    ) : this(
        dependencies.listCharactersInSceneController,
        dependencies.listAvailableCharactersToIncludeInSceneController,
        dependencies.includeCharacterInSceneController,
        dependencies.listAvailableArcSectionsToCoverInSceneController,
        dependencies.coverArcSectionsInSceneController,
        dependencies.createArcSectionController,
        dependencies.removeCharacterFromSceneController,
        dependencies.setMotivationForCharacterInSceneController,
        dependencies.assignRoleToCharacterInSceneController,
        SceneCharactersPresenter(view).apply {
            listensTo(dependencies.includedCharacterInSceneNotifier)
            listensTo(dependencies.removedCharacterFromSceneNotifier)
            listensTo(dependencies.renamedCharacterInSceneNotifier)
            listensTo(dependencies.characterArcSectionsCoveredBySceneNotifier)
            listensTo(dependencies.characterArcSectionUncoveredInSceneNotifier)
            listensTo(dependencies.characterRoleInSceneChangedNotifier)
        }
    )

    private var sceneId: Scene.Id? = null

    override fun getCharactersInScene(sceneId: Scene.Id) {
        this.sceneId = sceneId
        listCharactersInSceneController.listCharactersInScene(sceneId, presenter)
    }

    override fun getAvailableCharacters() {
        val sceneId = this.sceneId ?: return
        listAvailableCharactersToIncludeInSceneController.listAvailableCharacters(sceneId, presenter)
    }

    override fun addCharacter(characterId: Character.Id) {
        val sceneId = this.sceneId ?: return
        includeCharacterInSceneController.includeCharacterInScene(sceneId.uuid.toString(), characterId.uuid.toString())
    }

    override fun removeCharacter(characterId: Character.Id) {
        val sceneId = this.sceneId ?: return
        removeCharacterFromStoryEventController.removeCharacterFromScene(sceneId, characterId)
    }

    override fun getAvailableCharacterArcSections(characterId: Character.Id) {
        val sceneId = this.sceneId ?: return
        listAvailableArcSectionsToCoverInSceneController.listAvailableSectionsToCoverForCharacterInScene(
            characterId,
            sceneId,
            presenter
        )
    }

    override fun coverCharacterArcSectionInScene(
        characterId: Character.Id,
        characterArcSectionIds: List<String>,
        sectionsToUnCover: List<String>
    ) {
        val sceneId = this.sceneId ?: return
        coverArcSectionsInSceneController.coverCharacterArcSectionInScene(
            sceneId.uuid.toString(),
            characterId.uuid.toString(),
            characterArcSectionIds,
            sectionsToUnCover
        )
    }

    override fun createArcSectionToCoverInScene(
        characterId: Character.Id,
        themeId: Theme.Id,
        sectionTemplateId: CharacterArcTemplateSection.Id,
        initialValue: String
    ) {
        val sceneId = this.sceneId ?: return
        createArcSectionController.createArcSectionAndCoverInScene(
            characterId.uuid.toString(),
            themeId.uuid.toString(),
            sectionTemplateId.uuid.toString(),
            initialValue,
            sceneId.uuid.toString()
        )
    }

    override fun assignRole(characterId: Character.Id, role: RoleInScene) {
        val sceneId = this.sceneId ?: return
        assignRoleToCharacterInSceneController.assignRole(sceneId, characterId, role)
    }

    override fun clearRole(characterId: Character.Id) {
        val sceneId = this.sceneId ?: return
        assignRoleToCharacterInSceneController.clearRole(sceneId, characterId)
    }

    override fun setMotivation(characterId: Character.Id, motivation: String) {
        val sceneId = this.sceneId ?: return
        setMotivationForCharacterInSceneController.setMotivationForCharacter(
            sceneId.uuid.toString(),
            characterId.uuid.toString(),
            motivation
        )
    }

    override fun resetMotivation(characterId: Character.Id) {
        val sceneId = this.sceneId ?: return
        setMotivationForCharacterInSceneController.clearMotivationForCharacter(
            sceneId.uuid.toString(),
            characterId.uuid.toString()
        )
    }

}