package com.soyle.stories.characterarc.createArcSectionDialog

interface CreateArcSectionDialogViewListener {

    fun getValidState(themeUUID: String, characterUUID: String)
    fun createArcSection(characterId: String, themeId: String, sectionTemplateId: String, sceneId: String, description: String)
    fun modifyArcSection(characterId: String, themeId: String, arcSectionId: String, sceneId: String, description: String)

}