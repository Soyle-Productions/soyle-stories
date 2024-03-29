package com.soyle.stories.common.components.asyncMenuButton

import com.soyle.stories.common.scopedListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.layout.Region
import tornadofx.*

/**
 * Only loads items when shown.  Clears them when closed to allow for up-to-date items.
 */
class AsyncMenuButton<T> : Fragment() {

    companion object {
        fun <T> EventTarget.asyncMenuButton(op: AsyncMenuButton<T>.() -> Unit = {}): AsyncMenuButton<T> =
            FX.find<AsyncMenuButton<T>>(FX.defaultScope).also {
                add(it.root)
                it.op()
            }
    }

    val textProperty = titleProperty
    var text by textProperty

    val loadingLabelProperty = SimpleStringProperty("")
    var loadingLabel by loadingLabelProperty

    val onLoadProperty = SimpleObjectProperty<() -> Unit> {}
    var onLoad by onLoadProperty

    val sourceProperty = SimpleObjectProperty<List<T>>()
    var source by sourceProperty

    private var mapToItems: (List<T>) -> List<MenuItem> = { listOf() }
    fun itemsWhenLoaded(mapping: (List<T>) -> List<MenuItem>) {
        mapToItems = mapping
    }

    override val root: Region = menubutton {
        textProperty().bind(titleProperty)
        loadItemsWhenShown()
        addItemsWhenLoaded()
    }

    private fun loadingItem() = MenuItem("").apply { textProperty().bind(loadingLabelProperty) }

    private fun MenuButton.loadItemsWhenShown() {
        setOnShowing { onLoad() }
    }

    private fun MenuButton.addItemsWhenLoaded() {
        scopedListener(sourceProperty) { list: List<T>? ->
            when (list) {
                null -> items.setAll(loadingItem())
                else -> items.setAll(mapToItems(list))
            }
        }
    }
}
