package com.soyle.stories.common

import com.soyle.stories.di.DI
import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.binding.BooleanExpression
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleSetProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.scene.control.*
import javafx.scene.control.skin.TextAreaSkin
import javafx.scene.text.Text
import kotlinx.coroutines.CoroutineScope
import tornadofx.*
import kotlin.reflect.KProperty1

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 9:49 PM
 */


/**
 * Listen for changes to this observable. Optionally only listen until the predicate returns true.
 * The lambda receives the changed value when the change occurs, which may be null,
 */
fun <T> ObservableValue<T>.onChangeUntil(until: (T?) -> Boolean, op: (T?) -> Unit) {
	val listener = object : ChangeListener<T> {
		override fun changed(observable: ObservableValue<out T>?, oldValue: T, newValue: T) {
			if (until(newValue)) {
				removeListener(this)
			}
			op(newValue)
		}
	}
	addListener(listener)
}

fun <T> ObservableValue<T>.onChangeUntil(until: BooleanExpression, op: (T?) -> Unit) {
	val listener = ChangeListener<T> { observable, oldValue, newValue -> op(newValue) }
	until.onChangeUntil({ it != true }) {
		if (it != true) removeListener(listener)
	}
	addListener(listener)
}

fun <T : Any> ObservableValue<T>.onChangeWithCurrent(op: (T?) -> Unit) {
	onChange(op)
	op(value)
}

fun <T, R, L : List<R>> ItemViewModel<T>.bindImmutableList(property: KProperty1<T, L>): SimpleListProperty<R> =
  bind { item?.let(property)?.toObservable().toProperty() } as SimpleListProperty<R>

fun <T, R, L : Set<R>> ItemViewModel<T>.bindImmutableSet(property: KProperty1<T, L>): SimpleSetProperty<R> =
  bind { item?.let(property)?.toObservable().toProperty() } as SimpleSetProperty<R>

fun <T, R, S, L : Map<R, S>> ItemViewModel<T>.bindImmutableMap(property: KProperty1<T, L>): SimpleMapProperty<R, S> =
  bind { item?.let(property)?.toObservable().toProperty() } as SimpleMapProperty<R, S>

fun <S> TableColumn<S, *>.setStaticColumnIndex(index: Int) {
	var preventLoop = false
	tableViewProperty().onChange { tableView ->
		tableView?.columns?.onChange {
			if (preventLoop) return@onChange
			val currentIndex = columns.indexOf(this)
			val desiredIndex: Int = calculateIndex(index)
			if (currentIndex != desiredIndex) {
				runLater {
					preventLoop = true
					tableView.columns.move(this@setStaticColumnIndex, desiredIndex)
					preventLoop = false
				}
			}
		}
	}
	val currentIndex = columns.indexOf(this)
	val desiredIndex: Int = calculateIndex(index)
	if (currentIndex != desiredIndex) {
		preventLoop = true
		tableView.columns.move(this@setStaticColumnIndex, desiredIndex)
		preventLoop = false
	}
}

private fun <S> TableColumn<S, *>.calculateIndex(index: Int): Int {
	val columns = tableView?.columns?.toList() ?: return -1
	return if (index >= 0) {
		if (columns.size <= index) {
			// place column at end of column list
			columns.size - 1
		} else {
			index
		}
	} else {
		// negative indices are calculated from the end of the column list
		(columns.size + index).coerceAtLeast(0)
	}
}

fun <T> async(scope: ApplicationScope, block: suspend CoroutineScope.() -> T) {
	DI.resolve<ThreadTransformer>(scope).async { block() }
}
inline fun <T> async(scope: ProjectScope, noinline block: suspend CoroutineScope.() -> T) = async(scope.applicationScope, block)

private val TextAreaHiddenScrollBarsKey = "com.soyle.stories.hiddenScrollBars"

val TextArea.areScrollBarsHidden: Boolean
	get() = properties.getValue(TextAreaHiddenScrollBarsKey) as? Boolean ?: false

fun TextArea.hideScrollbars() {
	properties[TextAreaHiddenScrollBarsKey] = true
	val skinListener = skinListener@{ it: Skin<*>? ->
		if (it !is TextAreaSkin) return@skinListener
		it.children.filterIsInstance<ScrollPane>().firstOrNull()?.apply {
			vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
		}
	}
	skinProperty().onChange(skinListener)
	skinListener(skin)
}

/**
 * Calculates the current amount of rows in the textarea regardless
 * of "wordWrap" set to `true` or `false`.
 *
 * @return the current count of rows; `0` if the count could not be determined
 */
private fun TextArea.calculateRowCount(): Int {
	var currentRowCount = 0
	val helper = Text()
	/*
	 * Little performance improvement: If "wrapText" is set to false, then the
	 * list of paragraphs directly corresponds to the line count, otherwise we need
	 * to get creative...
	 */if (this.isWrapText) {
		// text needs to be on the scene
		val text: Text = lookup(".text") as? Text ?: return currentRowCount
		/*
	   * Now we just count the paragraphs: If the paragraph size is less
	   * than the current wrappingWidth then increment; Otherwise use our
	   * Text helper instance to calculate the change in height for the
	   * current paragraph with "wrappingWidth" set to the actual
	   * wrappingWidth of the TextArea text
	   */helper.font = font
		for (paragraph in paragraphs) {
			helper.text = paragraph.toString()
			val localBounds: Bounds = helper.boundsInLocal
			val paragraphWidth: Double = localBounds.width
			currentRowCount += if (paragraphWidth > text.wrappingWidth) {
				val oldHeight: Double = localBounds.height
				// this actually sets the automatic size adjustment into motion...
				helper.wrappingWidth = text.wrappingWidth
				val newHeight: Double = helper.boundsInLocal.height
				// ...and we reset it after computation
				helper.wrappingWidth = 0.0
				(newHeight / oldHeight).toInt()
			} else {
				1
			}
		}
	} else {
		currentRowCount = paragraphs.size
	}
	return currentRowCount
}

val TextArea.rowCountProperty
	get() = properties.getOrPut("com.soyle.stories.rowCountProperty") {
		wrapTextProperty().integerBinding(textProperty(), widthProperty()) {
			calculateRowCount()
		}
	} as IntegerBinding

val TextArea.rowCount
	get() = rowCountProperty.get()

val TreeView<*>.isEditing: Boolean
	get() = editingItem != null

val <T> TreeView<T>.editingCell: TreeCell<T>?
	get() = properties.getOrDefault("com.soyle.stories.treeView.editingCell", null) as? TreeCell<T>

fun <T> TreeView<T>.makeEditable(convertFromString: TreeCell<T>.(String, T?) -> T) {
	val self = this
	isEditable = true
	properties["tornadofx.editSupport"] = fun TreeCell<T>.(eventType: EditEventType, value: T?) {
		val cell = this
		graphic = when (eventType) {
			EditEventType.StartEdit -> {
				self.properties["com.soyle.stories.treeView.editingCell"] = this
				val rollbackText = text
				properties["com.soyle.stories.rollbackText"] = rollbackText
				text = null
				textfield(rollbackText) {
					action {
						commitEdit(convertFromString.invoke(cell, text, item))
					}
					focusedProperty().onChange {
						if (! it) {
							if (text != rollbackText) {
								commitEdit(convertFromString.invoke(cell, text, item))
							} else {
								cell.cancelEdit()
							}
						}
					}
					requestFocus()
					selectAll()
				}
			}
			EditEventType.CancelEdit -> {
				self.properties.remove("com.soyle.stories.treeView.editingCell", this)
				text = properties["com.soyle.stories.rollbackText"] as? String ?: ""
				null
			}
			EditEventType.CommitEdit -> {
				self.properties.remove("com.soyle.stories.treeView.editingCell", this)
				text = (graphic as? TextField)?.text ?: (properties["com.soyle.stories.rollbackText"] as? String) ?: ""
				null
			}
		}
	}
}