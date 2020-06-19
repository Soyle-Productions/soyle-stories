package com.soyle.stories.theme.deleteThemeDialog

import com.soyle.stories.di.DI
import com.soyle.stories.project.ProjectScope
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class DeleteThemeDialogScope(
    val projectScope: ProjectScope,
    val themeId: String,
    val themeName: String
) : Scope() {

    fun close() {
        FX.getComponents(this).values.forEach {
            if (it is EventTarget) it.removeFromParent()
        }
        DI.deregister(this)
        deregister()
    }

}