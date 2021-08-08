package com.soyle.stories.desktop.view.theme.valueWeb.opposition

import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.desktop.view.testframework.State
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.AddOppositionToValueWebControllerDouble
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.CreateOppositionValueFormLocaleMock
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueForm
import javafx.scene.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class `Create Opposition Value Form Design` : DesignTest() {

    private val addOppositionToValueWeb = AddOppositionToValueWebControllerDouble()

    override val node: Node
        get() = CreateOppositionValueForm(
            ValueWeb.Id(),
            {},
            CreateOppositionValueFormLocaleMock(),
            addOppositionToValueWeb
        )

    @State
    fun `default`() = verifyDesign()

    @State
    fun `error`() {
        addOppositionToValueWeb.onAddOpposition = { a, b, c ->
            CoroutineScope(Dispatchers.Main).async {
                throw Error("Failed to do the thing")
            }
        }

        verifyDesign()
    }
}