package com.soyle.stories.desktop.view.location.create

import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.desktop.view.testframework.State
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import javafx.scene.Node
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class CreateLocationDialogDesign : DesignTest() {

    private val dialog = CreateLocationDialogFactory().invoke()
    init {
        val primaryStage = primaryStage
        interact {
            dialog.show(primaryStage)
        }
    }

    override val node: Node by lazy {
        listWindows().asSequence().mapNotNull {
            if (! it.isShowing) return@mapNotNull null
            it.scene.root as? CreateLocationDialog.View
        }.single()
    }

    @State
    fun `default state`() = verifyDesign()

}