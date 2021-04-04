package com.soyle.stories.common.components.dataDisplay.list

import com.soyle.stories.common.components.ComponentsStyles.Companion.firstChild
import com.soyle.stories.common.components.ComponentsStyles.Companion.notFirstChild
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import tornadofx.*
import tornadofx.Stylesheet.Companion.box

class ListStyles : Stylesheet() {
    companion object {
        fun CssSelectionBlock.removeListViewBorder() {
            backgroundInsets = multi(box(0.px))
        }

        fun ListCell<*>.applyFirstChildPseudoClasses() {
            toggleClass(firstChild, item == listView.items.firstOrNull())
            toggleClass(notFirstChild, item != listView.items.firstOrNull())
        }

    }
}