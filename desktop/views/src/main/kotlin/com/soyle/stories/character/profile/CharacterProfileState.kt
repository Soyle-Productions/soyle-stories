package com.soyle.stories.character.profile

import com.soyle.stories.character.nameVariant.addNameVariant.CharacterNameVariantAddedNotifier
import com.soyle.stories.character.nameVariant.addNameVariant.CharacterNameVariantAddedReceiver
import com.soyle.stories.character.nameVariant.list.ListCharacterNameVariantsController
import com.soyle.stories.character.nameVariant.remove.CharacterNameVariantRemovedNotifier
import com.soyle.stories.character.nameVariant.remove.CharacterNameVariantRemovedReceiver
import com.soyle.stories.character.renameCharacter.CharacterRenamedNotifier
import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.common.guiUpdate
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed
import com.soyle.stories.domain.character.name.events.CharacterNameAdded
import com.soyle.stories.domain.character.name.events.CharacterNameRemoved
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import javafx.beans.Observable
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import tornadofx.*

class CharacterProfileState : ItemViewModel<CharacterProfileProps>(), CharacterNameVariantAddedReceiver,
    CharacterNameVariantRemovedReceiver, CharacterRenamedReceiver {

    val characterId: ReadOnlyProperty<Character.Id> = bind(CharacterProfileProps::characterId)
    val characterImageResource: ReadOnlyStringProperty = bind(CharacterProfileProps::imageResource)
    val characterDisplayName: ReadOnlyStringProperty = bind(CharacterProfileProps::name)

    internal val alternativeNames: ReadOnlyListProperty<String> = SimpleListProperty<String>(null).apply {
        characterId.onChange {
            set(null)
            if (it != null) {
                (scope as CharacterProfileScope).projectScope.get<ListCharacterNameVariantsController>()
                    .listCharacterNameVariants(it) {
                        guiUpdate {
                            set(it.toObservable())
                        }
                    }
            }
        }
    }

    internal val isCreatingName = SimpleBooleanProperty(false)
    internal val creationFailure = SimpleStringProperty(null)
    internal val executingNameChange = SimpleBooleanProperty(false)

    init {
        (scope as CharacterProfileScope).projectScope.run {
            this@CharacterProfileState listensTo get<CharacterNameVariantAddedNotifier>()
            this@CharacterProfileState listensTo get<CharacterRenamedNotifier>()
            this@CharacterProfileState listensTo get<CharacterNameVariantRemovedNotifier>()
        }
    }

    override suspend fun receiveCharacterNameVariantAdded(event: CharacterNameAdded) {
        if (event.characterId == item?.characterId) {
            guiUpdate {
                if (alternativeNames.value != null) {
                    alternativeNames.add(event.name)
                }
            }
        }
    }

    override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
        if (characterRenamed.characterId == item?.characterId) {
            guiUpdate {
                if (alternativeNames.value != null) {
                    val index = alternativeNames.indexOf(characterRenamed.oldName)
                    if (index < 0) return@guiUpdate
                    alternativeNames.add(index, characterRenamed.name)
                    alternativeNames.removeAt(index + 1)
                }
            }
        }
    }

    override suspend fun receiveCharacterNameVariantRemoved(event: CharacterNameRemoved) {
        if (event.characterId == item?.characterId) {
            guiUpdate {
                if (alternativeNames.value != null) {
                    alternativeNames.removeIf { it == event.name }
                }
            }
        }
    }

}