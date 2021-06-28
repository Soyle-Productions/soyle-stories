/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:34 PM
 */
package com.soyle.stories.characterarc.viewBaseStoryStructure

import com.soyle.stories.usecase.character.arc.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class ViewBaseStoryStructureController(
    private val threadTransformer: ThreadTransformer,
    private val viewBaseStoryStructure: ViewBaseStoryStructure
) {

    fun getBaseStoryStructure(
        characterId: String,
        themeId: String,
        viewBaseStoryStructureOutputPort: ViewBaseStoryStructure.OutputPort
    ) {
        val preparedCharacterId = UUID.fromString(characterId)
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            viewBaseStoryStructure.invoke(
                preparedCharacterId,
                preparedThemeId,
                viewBaseStoryStructureOutputPort
            )
        }
    }
}