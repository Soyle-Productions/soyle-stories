/**
 * Created by Brendan
 * Date: 3/3/2020
 * Time: 8:34 AM
 */
package com.soyle.stories.characterarc.characterComparison

interface CharacterComparisonViewListener {

    suspend fun getCharacterComparison(themeId: String, characterId: String)
    suspend fun addCharacterToComparison(themeId: String, characterId: String)
    suspend fun promoteCharacter(themeId: String, characterId: String)
    suspend fun demoteCharacter(themeId: String, characterId: String)
    suspend fun updateValue(sectionId: String, value: String)
    suspend fun setStoryFunction(themeId: String, characterId: String, targetCharacterId: String, storyFunction: String)
    suspend fun updateCentralMoralQuestion(themeId: String, question: String)
    suspend fun changeCharacterPropertyValue(themeId: String, characterId: String, property: String, value: String)
    suspend fun changeSharedPropertyValue(themeId: String, perspectiveCharacterId: String, targetCharacterId: String, property: String, value: String)
    suspend fun removeCharacterFromComparison(themeId: String, characterId: String)
}