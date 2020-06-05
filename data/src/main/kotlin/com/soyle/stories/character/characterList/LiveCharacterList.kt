package com.soyle.stories.character.characterList

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer

class LiveCharacterList(
  threadTransformer: ThreadTransformer,
  listAllCharacterArcs: ListAllCharacterArcs,
  buildNewCharacterNotifier: Notifier<BuildNewCharacter.OutputPort>,
  removeCharacterFromLocalStoryNotifier: Notifier<RemoveCharacterFromStory.OutputPort>,
  renameCharacterNotifier: Notifier<RenameCharacter.OutputPort>
): Notifier<CharacterListListener>() {

	private val loader = lazy {
		threadTransformer.async {
			listAllCharacterArcs.invoke(outputs)
		}
	}
	private var characters: List<CharacterItem>? = null

	private val outputs = object :
	  ListAllCharacterArcs.OutputPort,
	  BuildNewCharacter.OutputPort,
	  RemoveCharacterFromStory.OutputPort,
	  RenameCharacter.OutputPort
	{
		override fun receiveCharacterArcList(response: ListAllCharacterArcs.ResponseModel) {
			val characters = response.characters.keys.toList()
			this@LiveCharacterList.characters = characters
			notifyAll { it.receiveCharacterListUpdate(characters) }
		}

		override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
			val characters = characters!! + response
			this@LiveCharacterList.characters = characters
			notifyAll { it.receiveCharacterListUpdate(characters) }
		}

		override fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
			val characters = characters!!.filterNot { it.characterId == response.characterId }
			this@LiveCharacterList.characters = characters
			notifyAll { it.receiveCharacterListUpdate(characters) }
		}

		override fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
			val characters = characters!!.filterNot { it.characterId == response.characterId } + CharacterItem(response.characterId, response.newName)
			this@LiveCharacterList.characters = characters
			notifyAll { it.receiveCharacterListUpdate(characters) }
		}

		override fun receiveBuildNewCharacterFailure(failure: CharacterException) {}
		override fun receiveRemoveCharacterFromStoryFailure(failure: Exception) {}
		override fun receiveRenameCharacterFailure(failure: CharacterException) {}
	}

	init {
		buildNewCharacterNotifier.addListener(outputs)
		renameCharacterNotifier.addListener(outputs)
		removeCharacterFromLocalStoryNotifier.addListener(outputs)
	}

	override fun addListener(listener: CharacterListListener) {
		super.addListener(listener)
		val characters = characters
		if (characters == null) {
			synchronized(this) {
				if (! loader.isInitialized()) loader.value
			}
		} else {
			listener.receiveCharacterListUpdate(characters)
		}
	}

}