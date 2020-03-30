/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 5:03 PM
 */
package com.soyle.stories.characterarc.planCharacterArcDialog

interface PlanCharacterArcDialogViewListener {
    suspend fun planCharacterArc(characterId: String, name: String)
}