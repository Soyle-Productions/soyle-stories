package com.soyle.stories.scene.sceneDetails.includedCharacter

interface IncludedCharacterInSceneViewListener {

    fun removeCharacter()

    fun setMotivation(motivation: String)
    fun resetMotivation()
    fun openSceneDetails(sceneId: String)

    fun getAvailableCharacterArcSections()
    fun coverCharacterArcSectionInScene(
        characterArcSectionIds: List<String>,
        sectionsToUnCover: List<String>
    )

}