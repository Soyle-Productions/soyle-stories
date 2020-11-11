package com.soyle.stories.characterarc.addArcSectionToMoralArgument

interface AddArcSectionToMoralArgumentController {

    fun addCharacterArcSectionToMoralArgument(
        themeId: String,
        characterId: String,
        templateSectionId: String,
        indexInMoralArgument: Int? = null
    )

}