package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.Model
import com.soyle.stories.soylestories.ApplicationScope

class ProseEditorState : Model<ProseEditorScope, ProseEditorViewModel>(ProseEditorScope::class) {

    val versionNumber = bind(ProseEditorViewModel::versionNumber)
    val content = bind(ProseEditorViewModel::content)

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope
}