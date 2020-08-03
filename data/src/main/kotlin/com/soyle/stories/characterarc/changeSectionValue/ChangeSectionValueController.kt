package com.soyle.stories.characterarc.changeSectionValue

interface ChangeSectionValueController {

    fun changeDesire(themeId: String, characterId: String, desire: String)
    fun setPsychologicalWeakness(themeId: String, characterId: String, weakness: String)
    fun setMoralWeakness(themeId: String, characterId: String, weakness: String)

}