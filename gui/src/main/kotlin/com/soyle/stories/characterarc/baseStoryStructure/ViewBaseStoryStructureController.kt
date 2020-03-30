/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:34 PM
 */
package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.gui.ThreadTransformer
import java.util.*

class ViewBaseStoryStructureController(
    private val threadTransformer: ThreadTransformer,
    private val viewBaseStoryStructure: ViewBaseStoryStructure,
    private val viewBaseStoryStructureOutputPort: ViewBaseStoryStructure.OutputPort
) {

    fun getBaseStoryStructure(characterId: String, themeId: String) {
        executeUseCaseInBackground(
            UUID.fromString(characterId),
            UUID.fromString(themeId)
        )
    }

    private fun executeUseCaseInBackground(characterId: UUID, themeId: UUID) {
        threadTransformer.async {
            executeUseCase(characterId, themeId)
        }
    }

    private suspend fun executeUseCase(characterId: UUID, themeId: UUID)
    {
        viewBaseStoryStructure.invoke(
            characterId,
            themeId,
            viewBaseStoryStructureOutputPort
        )
    }
}