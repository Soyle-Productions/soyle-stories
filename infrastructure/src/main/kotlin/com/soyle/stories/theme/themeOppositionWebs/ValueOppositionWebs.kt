package com.soyle.stories.theme.themeOppositionWebs

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewListener
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.animation.Timeline
import javafx.animation.TranslateTransition
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.effect.DropShadow
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
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
                addClass(".content-pane")
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
                    label("[Value Selected]") {
                        isVisible = false
                    }
                    spacer()
                    button("Add Opposition") {
                        isVisible = false
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
                    addClass("value-web-list")
                    bindChildren(model.valueWebs) {
                        label(it.valueWebName) {
                            padding = Insets(5.0, 5.0, 5.0, 10.0)
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
    }
}