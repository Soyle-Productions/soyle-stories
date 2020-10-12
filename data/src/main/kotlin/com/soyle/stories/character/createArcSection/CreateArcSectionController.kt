package com.soyle.stories.character.createArcSection

interface CreateArcSectionController {

    fun createArcSectionAndCoverInScene(
        characterId: String,
        themeId: String,
        sectionTemplateId: String,
        value: String,
        sceneId: String
    )

}