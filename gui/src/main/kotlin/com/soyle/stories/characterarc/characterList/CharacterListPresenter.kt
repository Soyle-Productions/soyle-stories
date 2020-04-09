package com.soyle.stories.characterarc.characterList

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.characterarc.LocalCharacterArcException
import com.soyle.stories.characterarc.eventbus.EventBus
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:08 PM
 */
class CharacterListPresenter(
  private val threadTransformer: ThreadTransformer,
  private val view: CharacterListView,
  eventBus: EventBus
) : ListAllCharacterArcs.OutputPort, BuildNewCharacter.OutputPort, PlanNewCharacterArc.OutputPort,
  PromoteMinorCharacter.OutputPort, DeleteLocalCharacterArc.OutputPort, RemoveCharacterFromLocalStory.OutputPort,
  RenameCharacter.OutputPort, RenameCharacterArc.OutputPort {

	init {
		eventBus.buildNewCharacter.addListener(this)
		eventBus.planNewCharacterArc.addListener(this)
		eventBus.promoteMinorCharacter.addListener(this)
		eventBus.deleteLocalCharacterArc.addListener(this)
		eventBus.removeCharacterFromStory.addListener(this)
		eventBus.renameCharacter.addListener(this)
		eventBus.renameCharacterArc.addListener(this)
	}

	override fun receiveCharacterArcList(response: ListAllCharacterArcs.ResponseModel) {
		threadTransformer.gui {
			view.displayNewViewModel(
			  CharacterListViewModel(
				response.characters.map { (character, arcs) ->
					val characterId = character.characterId.toString()
					CharacterTreeItemViewModel(
					  characterId,
					  character.characterName,
					  false,
					  arcs.map { arc ->
						  CharacterArcItemViewModel(
							characterId,
							arc.themeId.toString(),
							arc.characterArcName
						  )
					  })
				}.sortedBy { it.name }
			  )
			)
		}
	}

	override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
		threadTransformer.gui {
			val viewModel = view.getViewModel()
			  ?: return@gui view.invalidate()

			view.displayNewViewModel(
			  viewModel.copy(
				characters = (viewModel.characters + CharacterTreeItemViewModel(
				  response.characterId.toString(),
				  response.characterName,
				  false,
				  emptyList()
				)).sortedBy { it.name }
			  )
			)
		}
	}

	override fun receivePlanNewCharacterArcResponse(response: CharacterArcItem) {
		CharacterArcItemViewModel(
		  response.characterId.toString(),
		  response.themeId.toString(),
		  response.characterArcName
		).let(this::addNewCharacterArcItem)
	}

	override fun receivePromoteMinorCharacterResponse(response: PromoteMinorCharacter.ResponseModel) {
		CharacterArcItemViewModel(
		  response.characterId.toString(),
		  response.themeId.toString(),
		  response.characterArcName
		).let(this::addNewCharacterArcItem)
	}

	private fun addNewCharacterArcItem(newItem: CharacterArcItemViewModel) {
		threadTransformer.gui {
			val viewModel = view.getViewModel()
			  ?: return@gui view.invalidate()

			val characterItem = viewModel.characters.find { it.id == newItem.characterId }
			  ?: return@gui view.invalidate()

			view.displayNewViewModel(
			  viewModel.copy(
				characters = viewModel.characters.minus(characterItem).plus(
				  characterItem.copy(
					isExpanded = true,
					arcs = characterItem.arcs + newItem
				  )
				).sortedBy { it.name }
			  )
			)
		}
	}

	override fun receiveDeleteLocalCharacterArcResponse(response: DeleteLocalCharacterArc.ResponseModel) {
		threadTransformer.gui {
			val viewModel = view.getViewModel()
			  ?: return@gui view.invalidate()

			val characterItem = viewModel.characters.find { it.id == response.characterId.toString() }
			  ?: return@gui view.invalidate()

			view.displayNewViewModel(
			  viewModel.copy(
				characters = viewModel.characters.minus(characterItem).plus(
				  characterItem.copy(
					arcs = characterItem.arcs.filterNot { it.themeId == response.themeId.toString() }
				  )
				).sortedBy { it.name }
			  )
			)
		}
	}

	override fun receiveRemoveCharacterFromLocalStoryResponse(response: RemoveCharacterFromLocalStory.ResponseModel) {
		threadTransformer.gui {
			val viewModel = view.getViewModel()
			  ?: return@gui view.invalidate()

			val characterItem = viewModel.characters.find { it.id == response.characterId.toString() }
			  ?: return@gui

			view.displayNewViewModel(
			  viewModel.copy(
				characters = viewModel.characters.minus(characterItem)
			  )
			)
		}
	}

	override fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
		threadTransformer.gui {
			val viewModel = view.getViewModel()
			  ?: return@gui view.invalidate()

			val characterItem = viewModel.characters.find { it.id == response.characterId.toString() }
			  ?: return@gui

			view.displayNewViewModel(
			  viewModel.copy(
				characters = viewModel.characters.minus(characterItem).plus(
				  characterItem.copy(
					name = response.newName
				  )
				).sortedBy { it.name }
			  )
			)
		}
	}

	override fun receiveRenameCharacterArcResponse(response: RenameCharacterArc.ResponseModel) {
		threadTransformer.gui {
			val viewModel = view.getViewModel()
			  ?: return@gui view.invalidate()

			val characterItem = viewModel.characters.find { it.id == response.characterId.toString() }
			  ?: return@gui

			view.displayNewViewModel(
			  viewModel.copy(
				characters = viewModel.characters.minus(characterItem).plus(
				  characterItem.copy(
					arcs = characterItem.arcs
                      .filterNot { it.themeId == response.themeId.toString() }
                      .plus(CharacterArcItemViewModel(response.characterId.toString(), response.themeId.toString(), response.newName))
				  )
				).sortedBy { it.name }
			  )
			)
		}
	}

	override fun receiveBuildNewCharacterFailure(failure: CharacterException) {}
	override fun receivePlanNewCharacterArcFailure(failure: Exception) {}
	override fun receivePromoteMinorCharacterFailure(failure: ThemeException) {}
	override fun receiveDeleteLocalCharacterArcFailure(failure: LocalCharacterArcException) {}
	override fun receiveRemoveCharacterFromLocalStoryFailure(failure: CharacterException) {}
	override fun receiveRenameCharacterFailure(failure: CharacterException) {}
	override fun receiveRenameCharacterArcFailure(failure: Exception) {}

}