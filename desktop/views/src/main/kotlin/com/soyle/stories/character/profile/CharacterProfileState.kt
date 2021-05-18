package com.soyle.stories.character.profile

import com.soyle.stories.character.nameVariant.addNameVariant.CharacterNameVariantAddedNotifier
import com.soyle.stories.character.nameVariant.addNameVariant.CharacterNameVariantAddedReceiver
import com.soyle.stories.character.nameVariant.list.ListCharacterNameVariantsController
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.events.CharacterNameVariantAdded
import javafx.beans.property.*
import tornadofx.*

class CharacterProfileState : ItemViewModel<CharacterProfileProps>(), CharacterNameVariantAddedReceiver {

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

}