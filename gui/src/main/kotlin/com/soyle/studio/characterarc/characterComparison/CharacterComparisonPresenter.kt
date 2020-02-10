package com.soyle.studio.characterarc.characterComparison

import com.soyle.studio.characterarc.compareCharacters.CompareCharacters

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:13 AM
 */
class CharacterComparisonPresenter(
    private val view: CharacterComparisonView
) : CompareCharacters.OutputPort {
    override fun receiveCompareCharactersResponse(response: CompareCharacters.CharacterComparison) {
        view.update {
            CharacterComparisonViewModel(
                response.themeId.toString(),
                response.centralMoralQuestion,
                response.sectionComps,
                response.characters.map {
                    CharacterDetailsViewModel()
                }
            )
        }
    }

    override fun receiveCompareCharactersFailure(failure: Exception) {
        throw failure
    }
}