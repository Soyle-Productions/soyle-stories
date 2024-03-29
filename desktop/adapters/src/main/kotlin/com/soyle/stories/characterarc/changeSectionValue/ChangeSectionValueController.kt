package com.soyle.stories.characterarc.changeSectionValue

import kotlinx.coroutines.Job

interface ChangeSectionValueController {

    fun changeDesire(themeId: String, characterId: String, desire: String)
    fun setPsychologicalWeakness(themeId: String, characterId: String, weakness: String)
    fun setMoralWeakness(themeId: String, characterId: String, weakness: String)
    fun changeValueOfArcSection(themeId: String, characterId: String, arcSectionId: String, value: String): Job
    fun changeValueOfArcSectionAndCoverInScene(themeId: String, characterId: String, arcSectionId: String, value: String, sceneId: String)

}