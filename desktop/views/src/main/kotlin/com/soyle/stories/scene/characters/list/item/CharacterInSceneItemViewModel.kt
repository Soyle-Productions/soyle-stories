package com.soyle.stories.scene.characters.list.item

import com.soyle.stories.character.renameCharacter.CharacterRenamedNotifier
import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.character.events.CharacterAssignedRoleInScene
import com.soyle.stories.domain.scene.character.events.CharacterRoleInSceneCleared
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedNotifier
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedReceiver
import com.soyle.stories.scene.charactersInScene.source.added.SourceAddedToCharacterInSceneNotifier
import com.soyle.stories.scene.charactersInScene.source.added.SourceAddedToCharacterInSceneReceiver
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import javafx.beans.binding.StringExpression
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import tornadofx.*
import java.util.logging.Logger

class CharacterInSceneItemViewModel(
    item: ObjectProperty<CharacterInSceneItem>,
    scope: Scope? = null,
    locale: CharacterInSceneItemLocale? = scope?.get()
) {

    private val _item = item
    var item: CharacterInSceneItem by _item
        private set

    val scene: Scene.Id
        get() = item.scene

    val character: Character.Id
        get() = item.characterId

    private val _name = _item.stringBinding { it?.characterName }
    val name: String by _name
    fun name(): StringExpression = _name

    private val _warning = stringBinding(
        _item,
        locale?.warning?.characterRemovedFromStory ?: stringProperty(""),
        locale?.warning?.characterNotInvolvedInAnyStoryEvents ?: stringProperty("")
    ) {
        val currentItem = get() ?: return@stringBinding ""
        when {
            currentItem.sources.isEmpty() -> locale?.warning?.characterNotInvolvedInAnyStoryEvents?.value.orEmpty()
            currentItem.project == null -> locale?.warning?.characterRemovedFromStory?.value.orEmpty()
            else -> ""
        }
    }

    fun warning(): ObservableValue<String> = _warning
    val warning: String by _warning


    private val _iconSource = _item.objectBinding { "" }
    val iconSource: String? by _iconSource
    fun iconSource(): ObservableValue<String?> = _iconSource

    private val _roleObject = _item.objectBinding { it?.roleInScene }

    private val _role = _roleObject.stringBinding {
        when (it) {
            RoleInScene.IncitingCharacter -> locale?.role?.incitingCharacter?.value.orEmpty()
            RoleInScene.OpponentCharacter -> locale?.role?.opponentCharacter?.value.orEmpty()
            else -> ""
        }
    }
    val role: String by _role
    fun role(): StringExpression = _role

    fun hasRole(roleInScene: RoleInScene): Boolean = _roleObject.value == roleInScene

    private val presenter = scope?.let { Presenter(it) }

    private inner class Presenter(val scope: Scope) {

        private val mainContext = scope.get<ThreadTransformer>().guiContext
        private suspend fun update(updater: (CharacterInSceneItem) -> CharacterInSceneItem) {
            withContext(mainContext) { item = updater(item) }
        }

        private inline fun <T> onUpdate(crossinline applyEvent: CharacterInSceneItem.(T) -> CharacterInSceneItem): suspend (T) -> Unit {
            return { event -> update { it.applyEvent(event) } }
        }

        private val characterRenamedReceiver =
            CharacterRenamedReceiver(onUpdate(CharacterInSceneItem::withEventApplied))

        private val characterRoleInSceneReceiver = CharacterRoleInSceneChangedReceiver { event ->
            update {
                event.events.fold(it) { item, nextEvent ->
                    when (nextEvent) {
                        is CharacterRoleInSceneCleared -> item.withEventApplied(nextEvent)
                        is CharacterAssignedRoleInScene -> item.withEventApplied(nextEvent)
                    }
                }
            }
        }

        private val sourcedAddedReceiver =
            SourceAddedToCharacterInSceneReceiver(onUpdate(CharacterInSceneItem::withEventApplied))

        init {
            characterRenamedReceiver listensTo scope.get<CharacterRenamedNotifier>()
            characterRoleInSceneReceiver listensTo scope.get<CharacterRoleInSceneChangedNotifier>()
            sourcedAddedReceiver listensTo scope.get<SourceAddedToCharacterInSceneNotifier>()
        }
    }


}