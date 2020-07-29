package com.soyle.stories.character.characterList

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.buildNewCharacter.CreatedCharacterReceiver
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcsByCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.character
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter
import java.util.*

class LiveCharacterList(
    projectId: String,
    threadTransformer: ThreadTransformer,
    listAllCharacterArcs: ListAllCharacterArcs,
    createdCharacterNotifier: Notifier<CreatedCharacterReceiver>,
    removeCharacterFromLocalStoryNotifier: Notifier<RemoveCharacterFromStory.OutputPort>,
    renameCharacterNotifier: Notifier<RenameCharacter.OutputPort>
) : Notifier<CharacterListListener>() {

    private val projectId = UUID.fromString(projectId)

    private val loader = lazy {
        threadTransformer.async {
            listAllCharacterArcs.invoke(this@LiveCharacterList.projectId, outputs)
        }
    }
    private var characters: List<CharacterItem>? = null

    private val outputs = object :
        ListAllCharacterArcs.OutputPort,
        CreatedCharacterReceiver,
        RemoveCharacterFromStory.OutputPort,
        RenameCharacter.OutputPort {
        override suspend fun receiveCharacterArcList(response: CharacterArcsByCharacter) {
            val characters = response.characters.map { it.character }
            this@LiveCharacterList.characters = characters
            notifyAll { it.receiveCharacterListUpdate(characters) }
        }

        override suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter) {
            val characters = characters!! + CharacterItem(
                createdCharacter.characterId,
                createdCharacter.characterName,
                createdCharacter.mediaId
            )
            this@LiveCharacterList.characters = characters
            notifyAll { it.receiveCharacterListUpdate(characters) }
        }

        override fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
            val characters = characters!!.filterNot { it.characterId == response.characterId }
            this@LiveCharacterList.characters = characters
            notifyAll { it.receiveCharacterListUpdate(characters) }
        }

        override fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
            val characters = characters!!.filterNot { it.characterId == response.characterId } + CharacterItem(
                response.characterId,
                response.newName,
                null
            )
            this@LiveCharacterList.characters = characters
            notifyAll { it.receiveCharacterListUpdate(characters) }
        }

        override fun receiveRemoveCharacterFromStoryFailure(failure: Exception) {}
        override fun receiveRenameCharacterFailure(failure: CharacterException) {}
    }

    init {
        createdCharacterNotifier.addListener(outputs)
        renameCharacterNotifier.addListener(outputs)
        removeCharacterFromLocalStoryNotifier.addListener(outputs)
    }

    override fun addListener(listener: CharacterListListener) {
        super.addListener(listener)
        val characters = characters
        if (characters == null) {
            synchronized(this) {
                if (!loader.isInitialized()) loader.value
            }
        } else {
            listener.receiveCharacterListUpdate(characters)
        }
    }

}