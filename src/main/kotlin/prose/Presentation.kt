package com.soyle.stories.desktop.config.prose

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.prose.proseEditor.ProseEditorController
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorState
import com.soyle.stories.prose.proseEditor.ProseEditorViewListener

object Presentation {

    init {
        scoped<ProseEditorScope> {
            provide<ProseEditorViewListener> {
                ProseEditorController(
                    proseId,
                    get<ProseEditorState>(),
                    projectScope.get()
                )
            }
        }
    }

}