package com.soyle.stories.scene.coverArcSectionsInScene

interface CoverArcSectionsInSceneController {

    fun coverCharacterArcSectionInScene(sceneId: String, characterId: String, characterArcSectionIds: List<String>, sectionsToUnCover: List<String> = listOf())

}