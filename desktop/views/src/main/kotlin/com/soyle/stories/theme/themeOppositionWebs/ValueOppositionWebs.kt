package com.soyle.stories.theme.themeOppositionWebs

import com.soyle.stories.common.components.editableText
import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import com.soyle.stories.theme.deleteValueWebDialog.DeleteValueWebDialog
import com.soyle.stories.theme.themeOppositionWebs.Styles.Companion.selectedItem
import com.soyle.stories.theme.themeOppositionWebs.Styles.Companion.valueWebList
import com.soyle.stories.theme.themeOppositionWebs.components.oppositionValueCard
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewListener
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.*
import kotlin.math.max

class ValueOppositionWebs : View() {

    override val scope = super.scope as ValueOppositionWebsScope
    private val viewListener = resolve<ValueOppositionWebsViewListener>()
    private val model = resolve<ValueOppositionWebsModel>()

    private val toolWidth = SimpleDoubleProperty(0.0)

    private val isLarge = toolWidth.greaterThanOrEqualTo(largeBoundary)

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
            openCreateValueWebDialog()
        }
        anchorpane {
            addClass("populated")
            hiddenWhen { model.valueWebs.select { it.isEmpty().toProperty() } }
            vbox {
                hgrow = Priority.ALWAYS
                addClass("content-pane")
                visibleWhen { model.selectedValueWeb.isNotNull }
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
                            val nonBlankName = NonBlankString.create(text)
                            if (nonBlankName != null) {
                                viewListener.renameValueWeb(selectedValueWeb.valueWebId, nonBlankName)
                            }
                        }
                        onShowing {
                            if (model.errorSource.value == model.selectedValueWeb.value?.valueWebId) { model.errorSource.set(null) }
                        }
                        onShown {
                            model.errorSource.onChangeUntil({ !isShowing }) {
                                println("error source changed")
                                println("error message ${model.errorMessage.value}")
                                errorMessage = if (it == model.selectedValueWeb.value?.valueWebId) model.errorMessage.value
                                else null
                            }
                            model.errorMessage.onChangeUntil({ !isShowing }) {
                                println("error message changed")
                                println("error source ${model.errorSource.value}")
                                errorMessage = if (model.errorSource.value == model.selectedValueWeb.value?.valueWebId) it
                                else null
                            }
                        }
                        selectedValueWebName.onChange {
                            hide()
                        }
                    }
                    spacer()
                    menubutton("Actions") {
                        item("Delete") {
                            action {
                                val selectedValueWeb = model.selectedValueWeb.value ?: return@action
                                scope.projectScope.get<DeleteValueWebDialog>().show(selectedValueWeb.valueWebId, selectedValueWeb.valueWebName)
                            }
                        }
                    }
                }
                hbox {
                    padding = Insets(0.0, 0.0, 0.0, 10.0)
                    button("Add Opposition") {
                        action {
                            val valueWebId = model.selectedValueWeb.value?.valueWebId ?: return@action
                            viewListener.addOpposition(valueWebId)
                        }
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
                            val currentColumnCount = columnConstraints.size
                            if (w < smallBoundary && currentColumnCount == 2) columnConstraints.remove(secondColumnConstraint)
                            else if (w >= smallBoundary && currentColumnCount == 1) columnConstraints.add(secondColumnConstraint)
                            if (columnConstraints.size != currentColumnCount) {
                                minWidth = columnConstraints.sumByDouble { it.minWidth } + 20.0 + ((columnConstraints.size -1) * 10.0)
                            }
                            columnConstraints.forEach {
                                it.maxWidth = w / columnConstraints.size
                            }
                        }
                        if (width >= smallBoundary) {
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
                            openCreateValueWebDialog()
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

    private fun openCreateValueWebDialog() {
        val stage = Stage()
        val createValueWebForm = scope.projectScope.get<CreateValueWebForm.Factory>()
            .invoke(Theme.Id(scope.themeId)) { stage.close() }
        stage.initOwner(currentWindow)
        stage.scene = Scene(createValueWebForm)
        stage.initStyle(StageStyle.UTILITY)
        stage.show()
    }

    companion object {
        const val smallBoundary = 581
        const val largeBoundary = 900
    }
}
