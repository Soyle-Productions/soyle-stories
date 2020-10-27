package com.soyle.stories.theme.moralArgument

interface MoralArgumentViewListener {

    fun getValidState()
    fun outlineMoralArgument(characterId: String)
    fun getAvailableArcSectionTypesToAdd(characterId: String)
    fun addCharacterArcSectionType(characterId: String, sectionTemplateId: String)
    fun addCharacterArcSectionTypeAtIndex(characterId: String, sectionTemplateId: String, index: Int)
    fun setMoralProblem(problem: String)
    fun setThemeLine(themeLine: String)
    fun setValueOfArcSection(characterId: String, arcSectionId: String, value: String)

}