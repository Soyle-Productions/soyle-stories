package com.soyle.stories.theme.deleteValueWebDialog

import com.soyle.stories.di.DI
import com.soyle.stories.project.ProjectScope
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class DeleteValueWebDialogScope(
    val projectScope: ProjectScope,
    val valueWebId: String,
    val valueWebName: String
) : Scope() {

    fun close() {
        FX.getComponents(this).values.forEach {
            if (it is EventTarget) it.removeFromParent()
        }
        DI.deregister(this)
        deregister()
    }

}