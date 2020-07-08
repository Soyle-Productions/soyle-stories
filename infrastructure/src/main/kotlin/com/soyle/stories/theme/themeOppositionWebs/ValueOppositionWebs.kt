package com.soyle.stories.theme.themeOppositionWebs

import com.soyle.stories.common.components.editableText
import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.components.popOutEditBox
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import com.soyle.stories.theme.deleteValueWebDialog.DeleteValueWebDialog
import com.soyle.stories.theme.themeOppositionWebs.Styles.Companion.selectedItem
import com.soyle.stories.theme.themeOppositionWebs.Styles.Companion.valueWebList
import com.soyle.stories.theme.themeOppositionWebs.components.oppositionValueCard
import com.soyle.stories.theme.valueOppositionWebs.OppositionValueViewModel
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewListener
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.animation.Timeline
import javafx.animation.TranslateTransition
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.effect.DropShadow
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.util.Duration
import tornadofx.*
import kotlin.math.max

class ValueOppositionWebs : View() {

    override val scope = super.scope as ValueOppositionWebsScope
    private val viewListener = resolve<ValueOppositionWebsViewListener>()
    private val model = resolve<ValueOppositionWebsModel>()

    private val toolWidth = SimpleDoubleProperty(0.0)

    private val isLarge = toolWidth.greaterThanOrEqualTo(900.0)

    private val animatedMenuX = SimpleDoubleProperty(0.0)
    private val targetX = SimpleDoubleProperty(0.0)
    private val splitPointX = SimpleDoubleProperty(0.0)

    private val selectedValueWebName = model.selectedValueWeb.select { it.valueWebName.toProperty() }

    private var menu by singleAssign<Region>()

    override val root: Parent = stackpane {
        toolWidth.bind(widthProperty())
        emptyListDisplay(
            model.valueWebs.select { it.isNotEmpty().toProperty() },
            "".toProperty(),
            "Create First Value Web".toProperty()
        ) {
            scope.projectScope.get<CreateValueWebDialog>().show(scope.themeId.toString(), currentWindow)
        }
        anchorpane {
            addClass("populated")
            hiddenWhen { model.valueWebs.select { it.isEmpty().toProperty() } }
            vbox {
                hgrow = Priority.ALWAYS
                addClass("content-pane")
                hbox(spacing = 5.0, alignment = Pos.CENTER_LEFT) {
                    padding = Insets(10.0)
                    button {
                        hiddenWhen { isLarge }
                        managedProperty().bind(visibleProperty())
                        graphic = MaterialIconView(MaterialIcon.SORT, "16px")
                        action {
                            if (targetX.get() != 0.0) targetX.set(0.0)
                            else targetX.set(-menu.width)
                        }
                    }
                    editableText(selectedValueWebName) {
                        id = "ValueWebName"
                        setOnAction {
                            val selectedValueWeb = model.selectedValueWeb.value ?: return@setOnAction
                            val text = editedText ?: ""
                            if (text == selectedValueWeb.valueWebName) {
                                hide()
                                return@setOnAction
                            }
                            viewListener.renameValueWeb(selectedValueWeb.valueWebId, text)
                        }
                        onShowing {
                            if (model.errorSource.value == model.selectedValueWeb.value?.valueWebId) { model.errorSource.set(null) }
                        }
                        onShown {
                            model.errorSource.onChangeUntil({ !isShowing }) {
                                errorMessage = if (it == model.selectedValueWeb.value?.valueWebId) model.errorMessage.value
                                else null
                            }
                        }
                        selectedValueWebName.onChange {
                            hide()
                        }
                    }
                    spacer()
                    menubutton("Actions") {
                        visibleWhen { model.selectedValueWeb.isNotNull }
                        item("Delete") {
                            action {
                                val selectedValueWeb = model.selectedValueWeb.value ?: return@action
                                scope.projectScope.get<DeleteValueWebDialog>().show(selectedValueWeb.valueWebId, selectedValueWeb.valueWebName)
                            }
                        }
                    }
                }
                button("Add Opposition") {
                    visibleWhen { model.selectedValueWeb.isNotNull }
                    action {
                        val valueWebId = model.selectedValueWeb.value?.valueWebId ?: return@action
                        viewListener.addOpposition(valueWebId)
                    }
                }
                stackpane {
                    visibleWhen { model.selectedValueWeb.isNotNull }
                    emptyListDisplay(
                        model.oppositionValues.select { it.isNotEmpty().toProperty() },
                        "".toProperty(),
                        "Create First Opposition Value".toProperty()
                    ) {
                        val valueWebId = model.selectedValueWeb.value?.valueWebId ?: return@emptyListDisplay
                        viewListener.addOpposition(valueWebId)
                    }
                    gridpane {
                        hiddenWhen { model.oppositionValues.select { it.isEmpty().toProperty() } }
                        vgap = 10.0
                        hgap = 10.0
                        padding = Insets(10.0)
                        columnConstraints.add(ColumnConstraints().apply {
                            hgrow = Priority.SOMETIMES
                            isFillWidth = true
                        })
                        val secondColumnConstraint = ColumnConstraints().apply {
                            hgrow = Priority.SOMETIMES
                            isFillWidth = true
                        }
                        widthProperty().onChange { w ->
                            if (w < 300 && columnConstraints.size == 2) columnConstraints.remove(secondColumnConstraint)
                            else if (w >= 300 && columnConstraints.size == 1) columnConstraints.add(secondColumnConstraint)
                            columnConstraints.forEach {
                                it.maxWidth = w / columnConstraints.size
                            }
                        }
                        if (width >= 300) {
                            columnConstraints.add(secondColumnConstraint)
                        }
                        model.oppositionValues.addListener { oppositionValues, old, new ->
                            val oldSize = old?.size ?: 0
                            val newSize = new?.size ?: 0
                            if (oldSize < newSize) {
                                repeat(newSize - oldSize) {
                                    oppositionValueCard(it + oldSize, model, viewListener)
                                }
                            }
                        }
                    }
                }
                anchorpaneConstraints {
                    topAnchor = 0.0
                    bottomAnchor = 0.0
                    rightAnchor = 0.0
                    splitPointX.onChange {
                        leftAnchor = max(it, 0.0)
                        applyToNode(this@vbox)
                    }
                }
            }
            menu = vbox {
                style {
                    backgroundColor += Color.WHITE
                }
                hgrow = Priority.NEVER
                hbox(spacing = 5.0, alignment = Pos.CENTER_LEFT) {
                    padding = Insets(10.0)
                    label("Value Webs")
                    button {
                        graphic = MaterialIconView(MaterialIcon.ADD, "16px")
                        addClass("create-value-web-button")
                        action {
                            scope.projectScope.get<CreateValueWebDialog>().show(scope.themeId.toString(), currentWindow)
                        }
                    }
                }
                vbox {
                    addClass(valueWebList)
                    bindChildren(model.valueWebs) {
                        val isValueWebSelected = model.selectedValueWeb.value?.valueWebId == it.valueWebId

                        hyperlink(it.valueWebName) {
                            toggleClass(selectedItem, isValueWebSelected)
                            action {
                                model.selectedValueWeb.set(it)
                            }
                        }
                    }
                }
                usePrefWidth = true
                anchorpaneConstraints {
                    topAnchor = 0.0
                    bottomAnchor = 0.0
                    animatedMenuX.onChange {
                        leftAnchor = it
                        applyToNode(this@vbox)
                    }
                }
            }
        }
    }

    init {
        viewListener.getValidState()

        val timeline = Timeline()
        targetX.onChange {
            timeline.keyFrames.clear()
            timeline.keyframe(Duration.millis(250.0)) {
                keyvalue(animatedMenuX, it)
            }
            timeline.play()
        }
        splitPointX.bind(animatedMenuX.plus(menu.widthProperty()))
        isLarge.onChange {
            if (it) {
                animatedMenuX.set(0.0)
                targetX.set(0.0)
            }
        }
        model.selectedValueWeb.onChange {
            if (it != null) viewListener.selectValueWeb(it.valueWebId)
        }
    }
}
