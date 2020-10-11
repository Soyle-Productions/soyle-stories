package com.soyle.stories.characterarc.createArcSectionDialog

interface CreateArcSectionDialogViewListener {

    fun getValidState()
    fun createArcSection(characterArcId: String, templateId: String, description: String)
    fun modifyArcSection(characterArcId: String, templateId: String, description: String)

}