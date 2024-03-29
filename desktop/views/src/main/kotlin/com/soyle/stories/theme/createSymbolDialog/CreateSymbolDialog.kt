package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.addSymbolToTheme.SymbolAddedToThemeNotifier
import com.soyle.stories.theme.addSymbolToTheme.SymbolAddedToThemeReceiver
import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
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

    override val scope: ProjectScope = super.scope as ProjectScope

    private val viewListener = resolve<CreateSymbolDialogViewListener>()
    private val model = resolve<CreateSymbolDialogModel>()

    private val selectingExistingTheme = SimpleBooleanProperty(true)
    private val themeName = SimpleStringProperty("")
    private val symbolName = SimpleStringProperty("")
    private val preSelectedThemeId = SimpleStringProperty(null)
    private val themeId = SimpleStringProperty(null)
    private val oppositionId = SimpleStringProperty(null)

    private val createdSymbolNotifier = resolve<SymbolAddedToThemeNotifier>()
    private var onCreateSymbol: ((SymbolAddedToTheme) -> Unit)? = null

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
        val nonBlankSymbolName = NonBlankString.create(symbolName.get())
        val nonBlankThemeName = NonBlankString.create(themeName.get())
        if (selectingExistingTheme.get()) {
            if (themeId != null) {
                if (nonBlankSymbolName != null) {
                    awaitNewSymbol()
                    viewListener.createSymbol(themeId, nonBlankSymbolName)
                } else {
                    model.updateOrInvalidated {
                        copy(
                            errorMessage = "Symbol name cannot be blank",
                            errorCause = "SymbolName"
                        )
                    }
                }
            } else {
                model.updateOrInvalidated {
                    copy(
                        errorMessage = "No theme selected.",
                        errorCause = "ThemeSelection"
                    )
                }
            }
        } else {
            if (nonBlankSymbolName != null && nonBlankThemeName != null) {
                awaitNewSymbol()
                viewListener.createThemeAndSymbol(nonBlankThemeName, nonBlankSymbolName)
            } else {
                model.updateOrInvalidated {
                    copy(
                        errorMessage = "Name cannot be blank",
                        errorCause = if (nonBlankSymbolName == null) "SymbolName" else "ThemeName"
                    )
                }
            }
        }
    }

    private fun awaitNewSymbol() {
        val listener = object : SymbolAddedToThemeReceiver {
            override suspend fun receiveSymbolAddedToTheme(symbolAddedToTheme: SymbolAddedToTheme) {
                scope.applicationScope.get<ThreadTransformer>().gui {
                    onCreateSymbol?.invoke(symbolAddedToTheme)
                }
                createdSymbolNotifier.removeListener(this)
            }
        }
        createdSymbolNotifier.addListener(listener)
    }

    companion object {
        private var minimumWindowWidth: Double? = null

        operator fun invoke(
            scope: ProjectScope,
            themeId: String? = null,
            linkToOpposition: String? = null,
            parentWindow: Window? = null,
            onCreateSymbol: (SymbolAddedToTheme) -> Unit = {  }
        ) {
            val dialog = scope.get<CreateSymbolDialog>()
            dialog.onCreateSymbol = onCreateSymbol
            dialog.show(themeId, linkToOpposition, parentWindow)
        }
    }
}