package com.soyle.stories.desktop.view.theme.valueWeb

import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.desktop.view.testframework.State
import com.soyle.stories.desktop.view.theme.valueWeb.create.CreateValueWebFormLocaleMock
import com.soyle.stories.desktop.view.theme.valueWeb.create.`Create Value Web Form Access`.Companion.access
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import javafx.scene.Node

class `Create Value Web Form Design` : DesignTest() {

    private var onCreateNode: CreateValueWebForm.() -> Unit = {}

    override val node: CreateValueWebForm
        get() = CreateValueWebForm(
            Theme.Id(),
            {},
            CreateValueWebFormLocaleMock(),
            AddValueWebToThemeControllerDouble()
        ).apply(onCreateNode)

    @State
    fun `default`() = verifyDesign()

    @State
    fun `executing`() {
        onCreateNode = {
            access().nameInput.text = "Banana"
            tryToCreateValueWeb()
        }
        verifyDesign()
    }

    @State
    fun `failure`() {
        onCreateNode = {
            tryToCreateValueWeb()
        }
        verifyDesign()
    }


}