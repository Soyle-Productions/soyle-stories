package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.*

class CreateSymbolDialog : Fragment() {

    private val viewListener = resolve<CreateSymbolDialogViewListener>()
    private val model = resolve<CreateSymbolDialogModel>()

    private val selectingExistingTheme = SimpleBooleanProperty(true)
    private val themeName = SimpleStringProperty("")
    private val symbolName = SimpleStringProperty("")
    private val preSelectedThemeId = SimpleStringProperty(null)
    private val themeId = SimpleStringProperty(null)
    private val oppositionId = SimpleStringProperty(null)

    override val root: Parent = form {
        fieldset(labelPosition = Orientation.VERTICAL) {
            field {
                textProperty.bind(model.nameFieldLabel)
                textfield {
                    id = "name-input"
                    model.itemProperty().onChange {
                        decorators.toList().forEach { removeDecorator(it) }
                        if (it?.errorCause == "SymbolName" && it.errorMessage != null) addDecorator(
                            SimpleMessageDecorator(it.errorMessage, ValidationSeverity.Error)
                        )
                    }
                    symbolName.bind(textProperty())
                    action {
                        createSymbol()
                    }
                }
            }
            hbox(alignment = Pos.BASELINE_LEFT, spacing = 10.0) {
                addClass("theme-link")
                visibleWhen { preSelectedThemeId.isNull }
                managedProperty().bind(visibleProperty())
                field("Select Existing Theme") {
                    hgrow = Priority.ALWAYS
                    visibleWhen { selectingExistingTheme }
                    managedProperty().bind(visibleProperty())
                    menubutton {
                        fitToParentWidth()
                        visibleProperty().bind(this@field.visibleProperty())
                        textProperty().bind(themeId.select {
                            (model.themes.find { it.themeId == themeId.get() }?.themeName ?: "").toProperty()
                        })
                        model.itemProperty().onChange {
                            decorators.toList().forEach { removeDecorator(it) }
                            if (it?.errorCause == "ThemeSelection" && it.errorMessage != null) addDecorator(
                                SimpleMessageDecorator(it.errorMessage, ValidationSeverity.Error)
                            )
                        }
                        items.bind(model.themes) {
                            MenuItem(it.themeName).apply {
                                id = it.themeId
                                action {
                                    themeId.set(it.themeId)
                                }
                            }
                        }
                    }
                }
                field("Create New Theme") {
                    hgrow = Priority.ALWAYS
                    hiddenWhen { selectingExistingTheme }
                    managedProperty().bind(visibleProperty())
                    textfield {
                        fitToParentWidth()
                        visibleProperty().bind(this@field.visibleProperty())
                        themeName.bind(textProperty())
                        model.itemProperty().onChange {
                            decorators.toList().forEach { removeDecorator(it) }
                            if (it?.errorCause == "ThemeName" && it.errorMessage != null) addDecorator(
                                SimpleMessageDecorator(it.errorMessage, ValidationSeverity.Error)
                            )
                        }
                        action {
                            createSymbol()
                        }
                    }
                }
                button {
                    hgrow = Priority.NEVER
                    textProperty().bind(selectingExistingTheme.select {
                        (if (it == true) "Create New Theme"
                        else "Select Existing Theme").toProperty()
                    })
                    action { selectingExistingTheme.set(!selectingExistingTheme.get()) }
                    disableWhen { model.themes.select { it.isEmpty().toProperty() } }
                }
            }
        }
    }

    init {
        titleProperty.bind(model.title)
    }

    fun show(themeId: String?, linkToOpposition: String? = null, parentWindow: Window? = null) {
        if (currentStage?.isShowing == true) return
        preSelectedThemeId.set(themeId)
        this.themeId.set(themeId)
        this.oppositionId.set(linkToOpposition)
        openModal(
            StageStyle.DECORATED,
            Modality.APPLICATION_MODAL,
            escapeClosesWindow = true,
            owner = parentWindow
        )?.apply {
            if (minimumWindowWidth == null) {
                model.title.onChangeOnce {
                    val text = root.text(it)
                    minimumWindowWidth = text.layoutBounds.width + width
                    text.removeFromParent()
                    minWidth = minimumWindowWidth!!
                }
            } else {
                minWidth = minimumWindowWidth!!
            }
        }
        model.createdId.onChangeUntil({ it != null || currentStage?.isShowing != true }) {
            if (it != null) {
                close()
                val oppositionId = this.oppositionId.get()
                if (oppositionId != null) {
                    viewListener.linkToOpposition(it, oppositionId)
                }
            }
        }
        model.itemProperty().onChangeUntil({ currentStage?.isShowing != true }) {
            if (it?.themes.isNullOrEmpty()) selectingExistingTheme.set(false)
        }
        viewListener.getValidState()
    }

    private fun createSymbol() {
        val themeId = this.themeId.get()
        if (selectingExistingTheme.get()) {
            if (themeId != null) {
                viewListener.createSymbol(themeId, symbolName.get())
            } else {
                model.updateOrInvalidated {
                    copy(
                        errorMessage = "No theme selected.",
                        errorCause = "ThemeSelection"
                    )
                }
            }
        } else {
            viewListener.createThemeAndSymbol(themeName.get(), symbolName.get())
        }
    }

    companion object {
        private var minimumWindowWidth: Double? = null
    }
}