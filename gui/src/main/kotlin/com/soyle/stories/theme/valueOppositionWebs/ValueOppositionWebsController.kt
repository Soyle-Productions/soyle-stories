package com.soyle.stories.theme.valueOppositionWebs

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ListValueWebsInTheme
import java.util.*

class ValueOppositionWebsController(
    themeId: String,
    private val threadTransformer: ThreadTransformer,
    private val listValueWebsInTheme: ListValueWebsInTheme,
    private val listValueWebsInThemeOutputPort: ListValueWebsInTheme.OutputPort
) : ValueOppositionWebsViewListener {

    private val themeId = UUID.fromString(themeId)

    override fun getValidState() {
        threadTransformer.async {
            listValueWebsInTheme.invoke(
                themeId,
                listValueWebsInThemeOutputPort
            )
        }
    }

}