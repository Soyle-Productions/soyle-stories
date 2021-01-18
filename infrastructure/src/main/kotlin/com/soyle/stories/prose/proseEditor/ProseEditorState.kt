package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.Model
import com.soyle.stories.soylestories.ApplicationScope

class ProseEditorState : Model<ProseEditorScope, ProseEditorViewModel>(ProseEditorScope::class) {

    val versionNumber = bind(ProseEditorViewModel::versionNumber)
    val content = bind(ProseEditorViewModel::content)
    val mentionQueryState = bind(ProseEditorViewModel::mentionQueryState)
    val isLocked = bind(ProseEditorViewModel::isLocked)
    val replacementOptions = bind(ProseEditorViewModel::replacementOptions)

    override fun viewModel(): ProseEditorViewModel? {
        return item?.copy(content = content.value)
    }

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope
}