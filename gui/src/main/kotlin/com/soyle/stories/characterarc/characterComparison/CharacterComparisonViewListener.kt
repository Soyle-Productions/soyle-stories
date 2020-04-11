/**
 * Created by Brendan
 * Date: 3/3/2020
 * Time: 8:34 AM
 */
package com.soyle.stories.characterarc.characterComparison

interface CharacterComparisonViewListener {

    suspend fun getCharacterComparison(characterId: String)
    suspend fun addCharacterToComparison(characterId: String)
    suspend fun promoteCharacter(characterId: String)
    suspend fun demoteCharacter(characterId: String)
    suspend fun updateValue(sectionId: String, value: String)
    suspend fun setStoryFunction(characterId: String, targetCharacterId: String, storyFunction: String)
    suspend fun updateCentralMoralQuestion(question: String)
    suspend fun changeCharacterPropertyValue(characterId: String, property: String, value: String)
    suspend fun changeSharedPropertyValue(perspectiveCharacterId: String, targetCharacterId: String, property: String, value: String)
    suspend fun removeCharacterFromComparison(characterId: String)
}