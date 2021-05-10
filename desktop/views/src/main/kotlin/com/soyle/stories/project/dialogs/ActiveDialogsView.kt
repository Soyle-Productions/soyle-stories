package com.soyle.stories.project.dialogs

import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogView
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import tornadofx.Component
import tornadofx.onChange

class ActiveDialogsView : Component() {

    override val scope: ProjectScope = super.scope as ProjectScope
    private val state = resolve<ActiveDialogsState>()

    init {
        scope.get<ActiveDialogsPresenter>()
        state.confirmDeleteCharacter.onChange {
            if (it?.isOpen == true) {
                resolve<DeleteCharacterDialogView>().show(it.dialogData!!)
                state.confirmDeleteCharacter.set(it.closed())
            }
        }
    }

}