package com.soyle.stories.scene.characters.list

import com.soyle.stories.character.create.createCharacterPrompt
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.common.*
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.scene.characters.include.selectStoryEvent.selectStoryEventPrompt
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemViewModel
import com.soyle.stories.scene.charactersInScene.assignRole.AssignRoleToCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.CharacterIncludedInSceneNotifier
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.SelectCharacterPrompt
import com.soyle.stories.scene.charactersInScene.involve.CharacterInvolvedInSceneNotifier
import com.soyle.stories.scene.charactersInScene.involve.CharacterInvolvedInSceneReceiver
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneNotifier
import com.soyle.stories.scene.renameScene.SceneRenamedNotifier
import com.soyle.stories.scene.renameScene.SceneRenamedReceiver
import com.soyle.stories.storyevent.character.remove.CharacterRemovedFromStoryEventNotifier
import com.soyle.stories.storyevent.coverage.uncover.StoryEventUncoveredBySceneNotifier
import com.soyle.stories.storyevent.coverage.uncover.StoryEventUncoveredBySceneReceiver
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensNotifier
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensReceiver
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import com.soyle.stories.usecase.scene.character.list.CharactersInScene
import javafx.beans.binding.StringExpression
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.VBox
import kotlinx.coroutines.withContext
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class CharactersInSceneViewModel(
    item: ObjectProperty<CharactersInScene>,
    val onEditCharacter: (CharacterInSceneItemViewModel) -> Unit = ::doNothing,
    private val scope: Scope? = null
) {

    private val _item = item
    var item: CharactersInScene
        get() = _item.value.copy(items = characters.map { it.item })
        private set(value) = _item.set(value)

    val sceneId: Scene.Id = _item.value.sceneId

    private val _name = _item.stringBinding { it?.sceneName }
    val name: String by _name
    fun name(): StringExpression = _name

    private val _selectedCharacter: ObjectProperty<CharacterInSceneItemViewModel?> = objectProperty(null)
    val selectedCharacter: ObservableValue<CharacterInSceneItemViewModel?>
        get() = _selectedCharacter

    fun onSelectCharacter(item: CharacterInSceneItemViewModel?) {
        _selectedCharacter.set(item)
    }

    private var _characterViewModelRegistration: Map<Character.Id, Pair<ObjectProperty<CharacterInSceneItem>, CharacterInSceneItemViewModel>> =
        mapOf()
    private val _characters = observableListOf<CharacterInSceneItemViewModel>().apply {
        scopedListener(_item) { newItem ->
            val updatedCharacterItemList = newItem?.items.orEmpty()
            val viewModelRegistration = getViewModels(_characterViewModelRegistration, updatedCharacterItemList)
            updatedCharacterItemList.forEach {
                viewModelRegistration.getValue(it.characterId).first.set(it)
            }
            _characterViewModelRegistration = viewModelRegistration
            setAll(updatedCharacterItemList.map { viewModelRegistration.getValue(it.characterId).second })
        }
    }
    val characters: ObservableList<CharacterInSceneItemViewModel> =
        FXCollections.unmodifiableObservableList(_characters)

    private fun getViewModels(
        registration: Map<Character.Id, Pair<ObjectProperty<CharacterInSceneItem>, CharacterInSceneItemViewModel>>,
        itemList: List<CharacterInSceneItem>
    ): Map<Character.Id, Pair<ObjectProperty<CharacterInSceneItem>, CharacterInSceneItemViewModel>> {
        return itemList.associate { item ->
            item.characterId to registration.getOrElse(item.characterId) {
                val itemState = objectProperty(item).apply {
                    onChange {
                        this@CharactersInSceneViewModel.item = this@CharactersInSceneViewModel.item
                    }
                }
                val itemViewModel = CharacterInSceneItemViewModel(itemState, scope)
                itemState to itemViewModel
            }
        }
    }

    private val presenter = scope?.let { Presenter(it) }

    val onAddCharacter: (SelectCharacterPrompt) -> Unit = presenter?.let { it::addCharacter } ?: ::doNothing
    val onRemoveCharacter: (CharacterInSceneItemViewModel) -> Unit =
        presenter?.let { it::removeCharacter } ?: ::doNothing
    val onToggleRole: (RoleInScene) -> Unit = presenter?.let { it::toggleRole } ?: ::doNothing

    private inner class Presenter(private val scope: Scope) {

        private val mainContext: CoroutineContext = scope.get<ThreadTransformer>().guiContext
        private fun <T : Any> update(updater: CharactersInScene.(T) -> CharactersInScene): suspend (T) -> Unit {
            return { event: T -> withContext(mainContext) { item = item.updater(event) } }
        }

        private val renameReceiver = SceneRenamedReceiver(update(CharactersInScene::withEventApplied))
        private val characterRemovedFromStoryReceiver =
            Receiver<CharacterRemovedFromStory>(update(CharactersInScene::withEventApplied))
        private val characterRemovedFromSceneReceiver =
            Receiver<CharacterRemovedFromScene>(update(CharactersInScene::withEventApplied))
        private val characterIncludedReceiver =
            Receiver<CharacterIncludedInScene>(update(CharactersInScene::withEventApplied))
        private val uncoveredStoryEventReceiver =
            StoryEventUncoveredBySceneReceiver(update(CharactersInScene::withEventApplied))
        private val characterRemovedFromStoryEventReceiver =
            Receiver<CharacterRemovedFromStoryEvent>(update(CharactersInScene::withEventApplied))
        private val storyEventNoLongerHappensReceiver =
            StoryEventNoLongerHappensReceiver(update(CharactersInScene::withEventApplied))
        private val characterInvolvedInSceneReceiver =
            CharacterInvolvedInSceneReceiver(update(CharactersInScene::withEventApplied))

        init {
            renameReceiver.listensTo(scope.get<SceneRenamedNotifier>())
            characterInvolvedInSceneReceiver.listensTo(scope.get<CharacterInvolvedInSceneNotifier>())
            characterRemovedFromStoryReceiver.listensTo(scope.get<RemovedCharacterNotifier>())
            characterRemovedFromSceneReceiver.listensTo(scope.get<RemovedCharacterFromSceneNotifier>())
            characterIncludedReceiver.listensTo(scope.get<CharacterIncludedInSceneNotifier>())
            uncoveredStoryEventReceiver.listensTo(scope.get<StoryEventUncoveredBySceneNotifier>())
            characterRemovedFromStoryEventReceiver.listensTo(scope.get<CharacterRemovedFromStoryEventNotifier>())
            storyEventNoLongerHappensReceiver.listensTo(scope.get<StoryEventNoLongerHappensNotifier>())
        }

        /**
         * (sceneId: Scene.Id, storyEventPrompt: SelectStoryEventPrompt, selectCharacterPrompt: SelectCharacterPrompt, createCharacterPrompt: CreateCharacterPrompt) -> Job
         */
        private val includeCharacterInScene = scope.get<IncludeCharacterInSceneController>()::includeCharacterInScene

        fun addCharacter(prompt: SelectCharacterPrompt) {
            includeCharacterInScene.invoke(
                item.sceneId,
                VBox().selectStoryEventPrompt(),
                prompt,
                createCharacterPrompt(scope)
            )
        }

        private val setCharacterRoleInScene = scope.get<AssignRoleToCharacterInSceneController>()

        fun toggleRole(roleInScene: RoleInScene) {
            println("")

        }

        fun removeCharacter(item: CharacterInSceneItemViewModel) {

        }

    }

}