package com.soyle.stories.theme.valueWeb.create

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.text.Caption.Companion.caption
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeController
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme
import javafx.application.Platform
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.scene.Parent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*
import javax.swing.text.Style

class CreateValueWebForm(
    // props
    private val themeId: Theme.Id,
    private val onCreateValueWeb: suspend (ValueWebAddedToTheme) -> Unit,
    // locale
    private val locale: CreateValueWebFormLocale,
    // actions
    private val addValueWebToTheme: AddValueWebToThemeController
) : VBox() {

    interface Factory {
        operator fun invoke(
            themeId: Theme.Id,
            onCreateValueWeb: suspend (ValueWebAddedToTheme) -> Unit = {}
        ): CreateValueWebForm
    }

    /* * * * * * * */
    // region State
    /* * * * * * * */

    private val nameProperty = stringProperty("")
    private val errorMessage = stringProperty(null)
    private val creatingValueWeb = booleanProperty(false)

    fun nameProperty() = nameProperty
    fun errorMessageProperty(): ReadOnlyStringProperty = errorMessage
    fun creatingValueWebProperty(): ReadOnlyBooleanProperty = creatingValueWeb

    // endregion

    /* * * * * * * * * * * */
    // region Initialization
    /* * * * * * * * * * * */

    // initialize styles
    init {
        addClass(Styles.createValueWebForm)
    }

    // initialize children
    init {
        fieldLabel(locale.name)
        vbox {
            addClass(Stylesheet.field)

            nameInput()
            errorCaption()
        }
    }

    // endregion

    /* * * * * * * * * * * */
    // region Sub Components
    /* * * * * * * * * * * */

    @ViewBuilder
    private fun Parent.nameInput() = textfield {
        textProperty().bindBidirectional(nameProperty)
        disableWhen(creatingValueWeb)

        action { tryToCreateValueWeb(text) }
        requestFocus()
    }

    @ViewBuilder
    private fun Parent.errorCaption() = caption() {
        addClass(Stylesheet.error)

        visibleWhen(errorMessage.isNotEmpty)
        textProperty().bind(errorMessage)
    }

    // endregion

    /* * * * * * * * * */
    // region Behaviors
    /* * * * * * * * * */

    /**
     * will attempt to create a value web with the current value of the [nameProperty].
     */
    fun tryToCreateValueWeb() {
        tryToCreateValueWeb(nameProperty.value)
    }

    private fun tryToCreateValueWeb(name: String) {
        errorMessage.unbind()
        val nonBlankName = NonBlankString.create(name) ?: return errorMessage.bind(locale.nameCannotBeBlank)
        tryToCreateValueWeb(nonBlankName)
    }

    private fun tryToCreateValueWeb(name: NonBlankString) {
        creatingValueWeb.value = true
        errorMessage.set(null)
        CoroutineScope(Dispatchers.JavaFx).launch {
            awaitValueWebAddedToTheme(name)
        }
    }

    private suspend fun awaitValueWebAddedToTheme(name: NonBlankString) {
        val addedValueWebToTheme = try {
            addValueWebToTheme.addValueWebToTheme(themeId.uuid.toString(), name) {}.await()
        } catch (t: Throwable) {
            errorMessage.set(t.localizedMessage)
            return
        } finally {
            creatingValueWeb.value = false
        }
        onCreateValueWeb(addedValueWebToTheme)
        nameProperty.set("")
    }

    // endregion

    override fun getUserAgentStylesheet(): String = Styles().externalForm

    class Styles : Stylesheet() {

        companion object {

            val createValueWebForm by cssclass()

            init {
                if (Platform.isFxApplicationThread()) importStylesheet<Styles>()
                else runLater { importStylesheet<Styles>() }
            }
        }

        init {
            createValueWebForm {
                spacing = 8.px

                field {
                    spacing = 4.px

                    error {
                        textFill = Color.RED
                        padding = box(0.em, 0.766666.em)
                    }
                }
            }
        }

    }

}