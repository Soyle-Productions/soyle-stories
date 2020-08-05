package com.soyle.stories.character.characterList

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.buildNewCharacter.CreatedCharacterReceiver
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.character.renameCharacter.RenamedCharacterReceiver
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcsByCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.character
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class LiveCharacterList(
    private val createdCharacterNotifier: Notifier<CreatedCharacterReceiver>,
    private val removedCharacterNotifier: Notifier<RemovedCharacterReceiver>,
    private val renamedCharacterNotifier: Notifier<RenamedCharacterReceiver>
) {

    fun addListener(listener: CharacterListListener) {
        createdCharacterNotifier.addListener(listener)
        renamedCharacterNotifier.addListener(listener)
        removedCharacterNotifier.addListener(listener)
    }

}