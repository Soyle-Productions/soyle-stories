package com.soyle.stories.common

import com.soyle.stories.di.DI
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.binding.BooleanExpression
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.skin.TextAreaSkin
import javafx.scene.control.skin.VirtualFlow
import javafx.scene.image.ImageView
import javafx.scene.input.ContextMenuEvent
import javafx.scene.text.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import tornadofx.*
import tornadofx.Stylesheet.Companion.selected
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

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

fun <T> async(scope: ApplicationScope, block: suspend CoroutineScope.() -> T): Job {
	return DI.resolve<ThreadTransformer>(scope).async { block() }
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

fun <T : Node> T.existsWhen(expr: ObservableValue<Boolean>): T {
	visibleWhen(expr)
	managedProperty().cleanBind(visibleProperty())
	return this
}
fun <T : Node> T.existsWhen(expr: () -> ObservableValue<Boolean>): T = existsWhen(expr())
var <T : Node> T.exists: Boolean
	get() = visibleProperty().get() && managedProperty().get()
	set(value) {
		visibleProperty().set(value)
		managedProperty().set(value)
	}

fun Node.onLoseFocus(op: () -> Unit) = focusedProperty().onChange { if (! it) op() }

val <T> ListView<T>.cells: LinkedHashSet<ListCell<T>>
	get() = properties.getOrPut("com.soyle.stories.listview.cells") { LinkedHashSet<ListCell<T>>() } as LinkedHashSet<ListCell<T>>

fun <T> ListView<T>.sizeToFitItems(maximumVisibleItems: Double = 11.5)
{
	val ROW_HEIGHT = (childrenUnmodifiable.firstOrNull() as? VirtualFlow<ListCell<T>>)?.firstVisibleCell?.height ?: 24.0
	val currentPrefHeight = prefHeight
	prefHeight = (items.size * ROW_HEIGHT) + 2.0
	val currentMaxHeight = maxHeight
	maxHeight = 2.0 + maximumVisibleItems * ROW_HEIGHT
	if (prefHeight != currentPrefHeight || maxHeight != currentMaxHeight) {
		requestLayout()
	}
}

/**
 * Enables file-level values to have cssclass delegate
 */
operator fun CssClassDelegate.getValue(nothing: Nothing?, property: KProperty<*>): CssRule = getValue(fileObj, property)
private val fileObj = Any()

fun Node.applyContextMenu(contextMenu: ContextMenu, onRequested: (ContextMenuEvent?) -> Unit = {})
{
	if (this is Control) {
		this.contextMenu = contextMenu
	} else {
		setOnContextMenuRequested {
			contextMenu.show(this, it.screenX, it.screenY)
			onRequested(it)
			it.consume()
		}
	}
}

/**
 * if the image is finished loading, the listener is called immediately.  Otherwise, when the image finishes loading or
 * fails to load, the listener will be called.  If the image fails to load, [error] will be true.
 */
@OptIn(ExperimentalContracts::class)
fun ImageView.onImageLoadingDone(listener: (error: Boolean) -> Unit) {
	contract {
		callsInPlace(listener, InvocationKind.EXACTLY_ONCE)
	}
	var wasCalled = false
	val exactlyOneCallListener = { error: Boolean ->
		if (! wasCalled) {
			wasCalled = true
			listener(error)
		}
	}
	onImageLoadingDoneExactlyOnce(exactlyOneCallListener)
}

private fun ImageView.onImageLoadingDoneExactlyOnce(listener: (error: Boolean) -> Unit) {
	if (image.isError) listener(true)
	else if (image.progress == 1.0) listener(false)
	else {
		image.progressProperty().onChangeOnce {
			onImageLoadingDone(listener)
		}
		image.errorProperty().onChangeOnce {
			onImageLoadingDone(listener)
		}
	}
}

fun Node.makeSelectable(focusTraversable: Boolean = this.isFocusTraversable, selectionClass: CssRule? = null): BooleanProperty {
	val selectedProperty = try {
		this::class.memberFunctions.find { it.name == "selectedProperty" }?.call() as? BooleanProperty ?: SimpleBooleanProperty(false)
	} catch (t: Throwable) {
		SimpleBooleanProperty(false)
	}

	properties["selectedProperty"] = selectedProperty

	isFocusTraversable = focusTraversable
	if (focusTraversable) {
		focusedProperty().onChange {
			if (it) {

			}
		}
	}
	onLeftClick { selectedProperty.set(true) }
	if (selectionClass != null) toggleClass(selectionClass, selectedProperty)
	toggleClass(selected, selectedProperty)

	return selectedProperty
}
