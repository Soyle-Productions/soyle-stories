package com.soyle.stories.character.profile

import com.soyle.stories.character.nameVariant.addNameVariant.CharacterNameVariantAddedNotifier
import com.soyle.stories.character.nameVariant.addNameVariant.CharacterNameVariantAddedReceiver
import com.soyle.stories.character.nameVariant.list.ListCharacterNameVariantsController
import com.soyle.stories.character.nameVariant.remove.CharacterNameVariantRemovedNotifier
import com.soyle.stories.character.nameVariant.remove.CharacterNameVariantRemovedReceiver
import com.soyle.stories.character.nameVariant.rename.CharacterNameVariantRenamedNotifier
import com.soyle.stories.character.nameVariant.rename.CharacterNameVariantRenamedReceiver
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.events.CharacterNameVariantAdded
import com.soyle.stories.domain.character.events.CharacterNameVariantRemoved
import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed
import javafx.beans.property.*
import tornadofx.*

class CharacterProfileState : ItemViewModel<CharacterProfileProps>(), CharacterNameVariantAddedReceiver,
    CharacterNameVariantRenamedReceiver, CharacterNameVariantRemovedReceiver {

    val characterId: ReadOnlyProperty<Character.Id> = bind(CharacterProfileProps::characterId)
    val characterImageResource: ReadOnlyStringProperty = bind(CharacterProfileProps::imageResource)
    val characterDisplayName: ReadOnlyStringProperty = bind(CharacterProfileProps::name)

    internal val alternativeNames: ReadOnlyListProperty<String> = SimpleListProperty<String>(null).apply {
        characterId.onChange {
            set(null)
            if (it != null) {
                (scope as CharacterProfileScope).projectScope.get<ListCharacterNameVariantsController>()
                    .listCharacterNameVariants(it) {
                        runLater {
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
            this@CharacterProfileState listensTo get<CharacterNameVariantRenamedNotifier>()
            this@CharacterProfileState listensTo get<CharacterNameVariantRemovedNotifier>()
        }
    }

    override suspend fun receiveCharacterNameVariantAdded(event: CharacterNameVariantAdded) {
        if (event.characterId == item?.characterId) {
            runLater {
                if (alternativeNames.value != null) {
                    alternativeNames.add(event.newVariant)
                }
            }
        }
    }

    override suspend fun receiveCharacterNameVariantRenamed(event: CharacterNameVariantRenamed) {
        if (event.characterId == item?.characterId) {
            runLater {
                if (alternativeNames.value != null) {
                    alternativeNames.replaceAll {
                        if (it == event.originalVariant.value) event.newVariant.value
                        else it
                    }
                }
            }
        }
    }

    override suspend fun receiveCharacterNameVariantRemoved(event: CharacterNameVariantRemoved) {
        if (event.characterId == item?.characterId) {
            runLater {
                if (alternativeNames.value != null) {
                    alternativeNames.removeIf { it == event.variant.value }
                }
            }
        }
    }

}