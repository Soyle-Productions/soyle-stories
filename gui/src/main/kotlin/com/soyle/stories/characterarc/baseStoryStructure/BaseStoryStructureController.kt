package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.characterarc.usecaseControllers.ChangeThematicSectionValueController

class BaseStoryStructureController(
    private val themeId: String,
    private val characterId: String,
    private val viewBaseStoryStructureController: ViewBaseStoryStructureController,
    private val changeThematicSectionValueController: ChangeThematicSectionValueController
) : BaseStoryStructureViewListener {

    override fun getBaseStoryStructure() {
        viewBaseStoryStructureController.getBaseStoryStructure(characterId, themeId)
    }

    override fun changeSectionValue(sectionId: String, value: String) {
        changeThematicSectionValueController.changeThematicSectionValue(sectionId, value)
    }

}