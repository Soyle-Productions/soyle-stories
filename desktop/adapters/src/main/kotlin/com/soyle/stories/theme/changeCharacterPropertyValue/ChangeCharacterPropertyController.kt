package com.soyle.stories.theme.changeCharacterPropertyValue

interface ChangeCharacterPropertyController {

    fun setArchetype(themeId: String, characterId: String, archetype: String)
    fun setAbility(themeId: String, characterId: String, ability: String)

}