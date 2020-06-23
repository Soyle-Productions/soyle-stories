package com.soyle.stories.theme.themeOppositionWebs

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.di.resolve
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewListener
import javafx.scene.Parent
import tornadofx.*

class ValueOppositionWebs : View() {

    override val scope = super.scope as ValueOppositionWebsScope
    private val viewListener = resolve<ValueOppositionWebsViewListener>()
    private val model = resolve<ValueOppositionWebsModel>()

    override val root: Parent = stackpane {
        emptyListDisplay(
            model.valueWebs.select { it.isNotEmpty().toProperty() },
            "".toProperty(),
            "".toProperty()
        ) {

        }
        vbox {
            hiddenWhen { model.valueWebs.select { it.isEmpty().toProperty() } }
            addClass("value-web-list")
            bindChildren(model.valueWebs) {
                label(it.valueWebName) {

                }
            }
        }
    }

    init {
        viewListener.getValidState()
    }
}