package com.soyle.stories.theme.themeList

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByTheme
import java.util.*

class ThemeListController(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val listSymbolsByTheme: ListSymbolsByTheme,
    private val listSymbolsByThemeOutputPort: ListSymbolsByTheme.OutputPort
) : ThemeListViewListener {

    private val projectId = UUID.fromString(projectId)

    override fun getValidState() {
        threadTransformer.async {
            listSymbolsByTheme.invoke(projectId, listSymbolsByThemeOutputPort)
        }
    }

}