package com.soyle.studio.characterarc.characterList

import com.soyle.studio.characterarc.listAllCharacterArcs.ListAllCharacterArcs

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:08 PM
 */
class CharacterListPresenter(
    private val view: CharacterListView
) : ListAllCharacterArcs.OutputPort {

    override fun receiveCharacterArcListResponse(response: List<ListAllCharacterArcs.CharacterSummary>) {
        view.update {
            CharacterListViewModel(
                response.map { characterSummary ->
                    CharacterItemViewModel(
                        characterSummary.characterId.toString(),
                        characterSummary.characterName,
                        characterSummary.characterArcs.map { arc ->
                            CharacterArcItemViewModel(arc.themeId.toString(), arc.displayName)
                        })
                }
            )
        }
    }

    override fun receiveCharacterArcListFailure(error: Exception) {}

}