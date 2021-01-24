package com.soyle.stories.project.dialogs

import com.soyle.stories.common.ProjectScopedModel

class ActiveDialogsState : ProjectScopedModel<ActiveDialogsViewModel>() {

    val confirmDeleteCharacter = bind(ActiveDialogsViewModel::confirmDeleteCharacter)

    override fun viewModel(): ActiveDialogsViewModel? {
        return item?.copy(
            confirmDeleteCharacter = confirmDeleteCharacter.value
        )
    }

    init {
        item = ActiveDialogsViewModel()
    }

}