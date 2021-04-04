package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme

interface SceneCharactersViewListener {
    fun getCharactersInScene(sceneId: Scene.Id)
    fun getAvailableCharacters()
    fun addCharacter(characterId: Character.Id)
    fun removeCharacter(characterId: Character.Id)

    fun setMotivation(characterId: Character.Id,motivation: String)
    fun resetMotivation(characterId: Character.Id)

    fun getAvailableCharacterArcSections(characterId: Character.Id)
    fun createArcSectionToCoverInScene(
        characterId: Character.Id,
        themeId: Theme.Id,
        sectionTemplateId: CharacterArcTemplateSection.Id,
        initialValue: String = ""
    )
    fun coverCharacterArcSectionInScene(
        characterId: Character.Id,
        characterArcSectionIds: List<String>,
        sectionsToUnCover: List<String>
    )
}