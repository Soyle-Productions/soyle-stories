package com.soyle.stories.characterarc.characterList

import com.soyle.stories.character.characterList.CharacterListListener
import com.soyle.stories.character.characterList.LiveCharacterList
import com.soyle.stories.characterarc.eventbus.CharacterArcEvents
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcsByCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:08 PM
 */
class CharacterListPresenter(
  private val threadTransformer: ThreadTransformer,
  private val view: CharacterListView,
  private val characterList: LiveCharacterList,
  characterArcEvents: CharacterArcEvents
) : PlanNewCharacterArc.OutputPort, CharacterListListener, ListAllCharacterArcs.OutputPort,
  PromoteMinorCharacter.OutputPort, DemoteMajorCharacter.OutputPort, RenameCharacterArc.OutputPort {

	init {
		characterList.addListener(this)
		characterArcEvents.planNewCharacterArc.addListener(this)
		characterArcEvents.promoteMinorCharacter.addListener(this)
		characterArcEvents.deleteLocalCharacterArc.addListener(this)
		characterArcEvents.renameCharacterArc.addListener(this)
	}

	override fun receiveCharacterListUpdate(characters: List<CharacterItem>) {
		threadTransformer.gui {
			val existingCharacters = view.getViewModel()?.characters
			  ?.associateBy {
				  it.id
			  } ?: emptyMap()

			view.displayNewViewModel(
			  CharacterListViewModel(
				characters.map {
					val characterId = it.characterId.toString()
					val existingCharacter = existingCharacters[characterId]
					CharacterTreeItemViewModel(
					  characterId,
					  it.characterName,
						"",
					  existingCharacter?.isExpanded ?: false,
					  existingCharacter?.arcs ?: emptyList()
					)
				}.sortedBy { it.name }
			  )
			)
		}
	}

	override suspend fun receiveCharacterArcList(response: CharacterArcsByCharacter) {
		threadTransformer.gui {
			view.displayNewViewModel(
			  CharacterListViewModel(
				response.characters.map { (it, arcs) ->
					val characterId = it.characterId.toString()
					CharacterTreeItemViewModel(
					  characterId,
					  it.characterName,
						"",
					  false,
					  arcs.map {
						  CharacterArcItemViewModel(
							characterId,
							it.themeId.toString(),
							it.characterArcName
						  )
					  }
					)
				}.sortedBy { it.name }
			  )
			)


		}
	}

	override suspend fun characterArcPlanned(response: CharacterArcItem) {
		CharacterArcItemViewModel(
			response.characterId.toString(),
			response.themeId.toString(),
			response.characterArcName
		).let(this::addNewCharacterArcItem)
	}

	override suspend fun themeNoted(response: CreatedTheme) {
		// do nothing
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

	override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
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

	override fun receivePromoteMinorCharacterFailure(failure: ThemeException) {}
	override fun receiveDemoteMajorCharacterFailure(failure: Exception) {}
	override fun receiveRenameCharacterArcFailure(failure: Exception) {}

}